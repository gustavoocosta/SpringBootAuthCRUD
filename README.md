

## JWT secret and roles

Set environment variable `JWT_SECRET` before running to secure tokens. Example:

```
export JWT_SECRET=super-secret-change-me
mvn -B package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

There are two seeded users:
- admin@example.com / admin123 (ROLE_ADMIN)
- user@example.com / user123 (ROLE_USER)

Use `/auth/login` to obtain a token and call `/api/admin/secret` (admin only).
