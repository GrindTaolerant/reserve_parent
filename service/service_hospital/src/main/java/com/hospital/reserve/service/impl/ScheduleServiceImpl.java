package com.hospital.reserve.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hospital.reserve.model.hosp.Department;
import com.hospital.reserve.model.hosp.Schedule;
import com.hospital.reserve.repository.ScheduleRepository;
import com.hospital.reserve.service.DepartmentService;
import com.hospital.reserve.service.HospitalService;
import com.hospital.reserve.service.ScheduleService;
import com.hospital.reserve.vo.hosp.BookingScheduleRuleVo;
import com.hospital.reserve.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> paramMap) {
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);

        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),
                schedule.getHosScheduleId());

        if(scheduleExist != null){
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);

            scheduleRepository.save(scheduleExist);
        }else{
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }


    }

    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //create Pageable, to create current page and number per page
        //0 is the first page
        Pageable pageable = PageRequest.of(page-1, limit);


        //create Example
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, matcher);


        Page<Schedule> all = scheduleRepository.findAll(example, pageable);

        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //check if exist
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        // search schedule hoscode and depcode
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // reorg with workDate
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria), // match var
                Aggregation.group("workDate")//divide in group
                        .first("workDate").as("workDate")
                //count
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),

                //sort
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                //make page
                Aggregation.skip((page-1) * limit),
                Aggregation.limit(limit)
        );


        AggregationResults<BookingScheduleRuleVo> aggResult = mongoTemplate.aggregate(aggregation,
                Schedule.class, BookingScheduleRuleVo.class);

        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResult.getMappedResults();

        Aggregation totalAgg = Aggregation.newAggregation(
            Aggregation.match(criteria),
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);

        int total = totalAggResults.getMappedResults().size();

        //convert date to week
        for(BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList){
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);

        String hosName = hospitalService.getHospName(hoscode);

        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hosName);
        result.put("baseMap", baseMap);

        return result;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(
                        hoscode,depcode,new DateTime(workDate).toDate());


        //loop list, set the hospital name, department name and the date
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });

        return scheduleList;
    }


    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "Sunday";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "Monday";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "Tuesday";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "Wednesday";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "Thursday";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "Friday";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "Saturday";
            default:
                break;
        }
        return dayOfWeek;
    }

    //pack schedule with its hospital, department name and work date.
    private void packageSchedule(Schedule schedule) {
        //set hospital
        schedule.getParam().put("hosname",hospitalService.getHospName(schedule.getHoscode()));
        //set department
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //set work date
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }


}
