# GrantFlow Pro

GrantFlow Pro is a full-stack grant and scholarship management platform for organizations that need a structured way to manage funding programs, applications, supporting documents, review decisions, audit logs, notifications, and analytics dashboards.

Live Demo: https://grantflow-frontend-var5.onrender.com  
Backend Health: https://grantflow-backend.onrender.com/actuator/health  
API Documentation: https://grantflow-backend.onrender.com/swagger-ui.html

## Problem

Many schools, nonprofits, foundations, and funding organizations manage grant or scholarship applications through disconnected tools such as spreadsheets, email threads, Google Forms, shared folders, and manual review notes. This creates delays, duplicated work, poor visibility, weak accountability, and inconsistent decision-making.

## Solution

GrantFlow Pro centralizes the funding workflow into one secure dashboard. Applicants can browse open programs, submit applications, upload supporting documents, save drafts, and track decisions. Administrators can manage programs, close or reopen funding opportunities, review submitted applications, score applicants, update decisions, view audit activity, and monitor application analytics.

## Features

### Applicant Portal

- Browse open funding programs
- Complete program-specific application prompts
- Upload supporting documents
- Save applications as drafts
- Edit and submit draft applications
- Track application status
- View application history
- Receive application-related notifications
- View applicant-specific analytics

### Admin Dashboard

- Create and manage funding programs
- Close and reopen programs
- View submitted applications
- Hide draft applications from admin review queues
- Review full application details
- View uploaded supporting documents
- Score applications using a review rubric
- Approve, reject, waitlist, or move applications under review
- View audit logs
- Monitor application status analytics

### Document Management

- Multipart file upload support
- Local document storage for development/demo use
- PostgreSQL metadata tracking for uploaded documents
- AWS S3-ready storage service architecture

### Platform Engineering

- JWT authentication
- Role-based access control
- PostgreSQL persistence
- Flyway database migrations
- Swagger/OpenAPI API documentation
- Spring Boot Actuator health checks
- Docker Compose local development
- GitHub Actions CI
- Render deployment
- Production environment variable configuration

## Demo Data

The deployed demo includes:

- 35 unique funding programs
- 25 seeded applications
- Mixed application statuses across submitted, under review, approved, rejected, and waitlisted
- Applicant-specific dashboard data
- Admin-level analytics and audit logs

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT
- Springdoc OpenAPI / Swagger
- Spring Boot Actuator
- AWS SDK for S3-ready storage
- Maven

### Frontend

- React
- TypeScript
- Vite
- Axios
- Recharts
- Lucide React
- CSS dashboard styling

### DevOps

- Docker
- Docker Compose
- GitHub Actions
- Render
- Render PostgreSQL

## Architecture

```text
React + TypeScript Frontend
        |
        | REST API requests
        v
Spring Boot Backend
        |
        | JWT Authentication + Role-Based Access
        v
Controller / Service / Repository Layers
        |
        | Spring Data JPA
        v
PostgreSQL Database
        |
        | Document metadata
        v
Local Storage or S3-ready Storage Service
