-- Electricity Billing System - database setup
-- Tables code ki SQL queries se banaye gaye hain (project ke saath .sql file nahi thi)

CREATE DATABASE IF NOT EXISTS ebs;
USE ebs;

-- Login accounts (Admin aur Customer dono)
CREATE TABLE login (
    meter_no  VARCHAR(20),
    username  VARCHAR(30),
    name      VARCHAR(50),
    password  VARCHAR(30),
    user      VARCHAR(20)          -- 'Admin' ya 'Customer'
);

-- Customer ki details
CREATE TABLE customer (
    name      VARCHAR(50),
    meter_no  VARCHAR(20) PRIMARY KEY,
    address   VARCHAR(100),
    city      VARCHAR(30),
    state     VARCHAR(30),
    email     VARCHAR(50),
    phone     VARCHAR(15)
);

-- Meter ki details
CREATE TABLE meter_info (
    meter_no        VARCHAR(20),
    meter_location  VARCHAR(20),
    meter_type      VARCHAR(20),
    phase_code      VARCHAR(10),
    bill_type       VARCHAR(20),
    days            VARCHAR(10)
);

-- Har month ka bill
CREATE TABLE bill (
    meter_no   VARCHAR(20),
    month      VARCHAR(20),
    units      VARCHAR(20),
    totalbill  VARCHAR(20),
    status     VARCHAR(20)         -- 'Paid' ya 'Not Paid'
);

-- Tax / rates (sirf EK row honi chahiye, warna bill double calculate hoga)
CREATE TABLE tax (
    cost_per_unit       VARCHAR(20),
    meter_rent          VARCHAR(20),
    service_charge      VARCHAR(20),
    service_tax         VARCHAR(20),
    swacch_bharat_cess  VARCHAR(20),
    fixed_tax           VARCHAR(20)
);

-- Sample rates (whole numbers hi daalna, code Integer.parseInt use karta hai)
INSERT INTO tax VALUES ('9', '47', '22', '57', '6', '18');

-- Pehla Admin account (username: admin, password: admin)
INSERT INTO login VALUES ('', 'admin', 'Admin', 'admin', 'Admin');
