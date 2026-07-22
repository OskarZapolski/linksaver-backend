# 💧 LinkDrop - Backend API

> The robust, AI-powered REST API serving the LinkDrop mobile ecosystem. Built to securely manage user data, automatically extract web metadata, and intelligently categorize links.

**[📱 Click here to view the Mobile App (Frontend) repository](https://github.com/OskarZapolski/linksaver-mobile)**

---

## ✨ Core Features & Architecture

This backend is designed with a focus on security, automation, and clean architecture. It goes beyond a simple CRUD application by integrating advanced data processing workflows.

- **🔒 Secure User Authentication (JWT):** Stateless, token-based authentication using JSON Web Tokens. Includes highly secure endpoint protection, role validation, and seamless user verification flows via Spring Security.
- **🕸️ Automated Data Scraping:** Whenever a user saves a raw URL, the backend automatically intercepts it and scrapes OpenGraph metadata (titles, descriptions, thumbnails) in real-time, providing a rich UI experience on the client side.
- **🤖 AI-Powered Categorization:** Integrated with a Large Language Model (AI) to analyze the scraped content and automatically assign relevant tags and categories to the saved links, completely eliminating manual sorting for the user.
- **🗄️ Relational Database Management:** Structured and optimized data storage using PostgreSQL, featuring complex entity relationships (Users, Links, Categories) and secure data isolation between users.

---

## 🛠️ Tech Stack

- **Core:** Java / Spring Boot
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Database:** PostgreSQL / Spring Data JPA (Hibernate)
- **Web Scraping:** JSoup
- **AI Integration:** OpenAI API (gpt-4o-mini)
- **API Documentation:** Swagger / OpenAPI

---
