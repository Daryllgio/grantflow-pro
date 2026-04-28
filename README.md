# GrantFlow Pro

GrantFlow Pro is a full-stack grant and scholarship operations platform for nonprofits. It helps organizations manage programs, applicants, application reviews, scoring rubrics, reviewer assignments, decision workflows, audit logs, notifications, and analytics dashboards.

## Problem

Small nonprofits often manage scholarship and grant applications through spreadsheets, emails, Google Forms, and manual review processes. This creates delays, duplicated work, poor visibility, weak accountability, and inconsistent decision-making.

## Solution

GrantFlow Pro centralizes the full funding workflow into one secure role-based dashboard for applicants, reviewers, program managers, and administrators.

## Features

- JWT authentication
- Role-based access control
- Applicant dashboard
- Reviewer dashboard
- Admin dashboard
- Program management
- Application submission
- Document metadata tracking
- Reviewer assignment
- Rubric-based scoring
- Application status workflow
- Audit logs
- In-app notifications
- Analytics dashboard
- PostgreSQL persistence
- Flyway database migrations
- Swagger/OpenAPI API documentation
- Spring Boot Actuator health checks
- Docker Compose local development
- GitHub Actions CI

## Tech Stack

Backend: Java 21, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, Flyway, Springdoc OpenAPI, Actuator, JWT

Frontend: React, TypeScript, Vite, Tailwind CSS, Axios, React Router, Recharts

DevOps: Docker, Docker Compose, GitHub Actions

## Local Development

Run:

docker compose up --build

Backend: http://localhost:8080

Frontend: http://localhost:5173

Swagger: http://localhost:8080/swagger-ui/index.html

Actuator health: http://localhost:8080/actuator/health

## Demo Accounts

Admin:
- admin@grantflow.dev
- password123

Reviewer:
- reviewer@grantflow.dev
- password123

Applicant:
- applicant@grantflow.dev
- password123
