# Medical Scheduling Center (ShangYiTong）

## Project Description
The Medical Scheduling Center Microservice is a Java Spring-based solution designed to facilitate the efficient management of hospital data and streamline the patient appointment scheduling process. This microservice aims to enhance the overall experience for both healthcare providers and patients by providing a user-friendly interface and seamless integration with existing hospital systems.

## Architecture
![MicroArchitecture](https://github.com/GrindTaolerant/reserve_parent/assets/66355314/caf9649b-36c6-406f-8057-eb5ba150e27e)

## Tech Stack
**Back End**  

Spring Boot  
Spring Cloud:  
  - Gateway: After nginx, route to APIs for Microservices
  - OpenFeign: Consume APIs and perform remote procedure calls (RPC) over HTTP
  - Nacos: Dynamic service discovery, configuration and service management platform  

MySQL  
Mybatis-plus:
  - BaseMapper: Simplify common database operations
  - xml configuration: Provides a way to configure mappings between Java objects and database tables  

Redis: Used as cache, set expiration time for payment QR code, verification code  
RabbitMQ: Provides a robust messaging system for services  
AWS S3: Store users authorization info on cloud  
Spring Mail: Send scheduling info emails to patients using SMTP  
MongoDB: NoSQL database, for storing hospital and scheduling information    
Docker: Deploy MongoDB, Redis, RabbitMQ  
EasyExcel: Simplify the process of working with Excel files  

**Front End**  

Node: js runtime environment   
Vue: Build user interfaces    
Element-ui: Provide ui elements  
Nuxt: Server-side Render  
NPM: Package manager based on node.js  
Echarts: For scheduling data visualization  



