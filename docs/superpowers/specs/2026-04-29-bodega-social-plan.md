# Plan de Implementación: Sistema de Gestión de Bodega Social

**Referencia:** `2026-04-29-bodega-social-design.md`  
**Stack:** Spring Boot + MongoDB + React + Vite + MUI  
**Hosting:** Railway (backend) + Netlify (frontend)

---

## Fase 1: Configuración del Backend

**1.1 Actualizar `pom.xml`**
- Agregar: `spring-boot-starter-security`, `spring-boot-starter-data-mongodb`
- Agregar: `jjwt-api`, `jjwt-impl`, `jjwt-jackson`
- Agregar: `poi-ooxml` (Apache POI para Excel)

**1.2 Configurar `application.properties`**
```properties
spring.data.mongodb.uri=${MONGODB_URI}
jwt.secret=${JWT_SECRET}
jwt.expiration=28800000
admin.email=${ADMIN_EMAIL}
admin.password=${ADMIN_PASSWORD}
frontend.url=${FRONTEND_URL}
```

**1.3 Crear estructura de paquetes**
- Crear las carpetas: `config`, `controller`, `service/impl`, `repository`, `model`, `dto/request`, `dto/response`, `security`

---

## Fase 2: Modelos

**2.1 Enum `Role`** — valores: `ADMIN`, `BODEGUERO`

**2.2 Enum `MovementType`** — valores: `ENTRADA`, `SALIDA`

**2.3 `User.java`**
- `@Document("users")`
- Campos: id, name, email, passwordHash, role, active, createdAt

**2.4 `Category.java`**
- `@Document("categories")`
- Campos: id, name

**2.5 `Product.java`**
- `@Document("products")`
- Campos: id, name, categoryId, hasSize, size, currentStock, criticalThreshold, active, createdAt

**2.6 `Movement.java`**
- `@Document("movements")`
- Campos: id, productId, productName (snapshot), categoryName (snapshot), type, quantity, userId, userName (snapshot), timestamp

---

## Fase 3: Repositorios

**3.1 `UserRepository`**
- `findByEmail(String email)`
- `existsByEmail(String email)`

**3.2 `CategoryRepository`**
- Sin metodos extra (usa los de MongoRepository)

**3.3 `ProductRepository`**
- `findByActiveTrue()`
- `findByCategoryIdAndActiveTrue(String categoryId)`
- `findByNameContainingIgnoreCaseAndActiveTrue(String name)`
- `findByCurrentStockLessThanCriticalThreshold()` — query personalizada con `@Query`

**3.4 `MovementRepository`**
- `findByTimestampBetween(LocalDateTime start, LocalDateTime end)`
- `findByTypeAndTimestampBetween(MovementType type, LocalDateTime start, LocalDateTime end)`

---

## Fase 4: Seguridad JWT

**4.1 `JwtUtil.java`**
- `generateToken(String userId, String email, String role)` → String
- `validateToken(String token)` → Claims
- `getUserIdFromToken(String token)` → String
- `getRoleFromToken(String token)` → String

**4.2 `JwtAuthFilter.java`** (extiende `OncePerRequestFilter`)
- Lee header `Authorization: Bearer <token>`
- Valida con JwtUtil
- Si valido, carga `UsernamePasswordAuthenticationToken` en `SecurityContextHolder`

**4.3 `SecurityConfig.java`**
- Deshabilitar CSRF
- Configurar CORS: permitir origen `${frontend.url}`, metodos GET/POST/PUT/PATCH, header Authorization
- Permitir sin autenticacion: `POST /api/auth/login`
- Rutas solo ADMIN: `/api/products/**` (POST/PUT/PATCH), `/api/users/**`
- El resto requiere autenticacion

**4.4 `DataInitializer.java`** (implementa `ApplicationRunner`)
- Al arrancar: si no existe ningun usuario con role ADMIN, crear uno con `admin.email` y `admin.password` de las variables de entorno, password hasheado con BCrypt

---

## Fase 5: DTOs

**Request:**
- `LoginRequest` — email, password
- `CategoryRequest` — name
- `ProductRequest` — name, categoryId, hasSize, size, currentStock, criticalThreshold
- `MovementRequest` — productId, type (MovementType), quantity
- `UserRequest` — name, email, password, role

**Response:**
- `AuthResponse` — token, name, role
- `CategoryResponse` — id, name
- `ProductResponse` — todos los campos + status ("OK" o "CRITICO" calculado)
- `MovementResponse` — todos los campos del movimiento
- `MonthlyReportResponse` — categoryName, totalUnits
- `UserResponse` — id, name, email, role, active (sin passwordHash)

---

## Fase 6: Módulo Auth

**`AuthService`** (interfaz)
- `login(LoginRequest request) → AuthResponse`

**`AuthServiceImpl`**
- Buscar usuario por email, lanzar excepcion si no existe o no esta activo
- Verificar password con `BCryptPasswordEncoder`
- Generar token con JwtUtil
- Retornar AuthResponse con token, name y role

**`AuthController`**
- `POST /api/auth/login` → `AuthResponse`

---

## Fase 7: Módulo Categorías

**`CategoryService`** (interfaz)
- `findAll() → List<CategoryResponse>`
- `create(CategoryRequest) → CategoryResponse`
- `update(String id, CategoryRequest) → CategoryResponse`

**`CategoryServiceImpl`** — implementacion directa con CategoryRepository

**`CategoryController`**
- `GET /api/categories` — todos los roles autenticados
- `POST /api/categories` — ADMIN y BODEGUERO
- `PUT /api/categories/{id}` — ADMIN y BODEGUERO

---

## Fase 8: Módulo Productos

**`ProductService`** (interfaz)
- `findAll(String categoryId, String status, String name) → List<ProductResponse>`
- `create(ProductRequest) → ProductResponse`
- `update(String id, ProductRequest) → ProductResponse`
- `deactivate(String id)`
- `getAlerts() → List<ProductResponse>`

**`ProductServiceImpl`**
- Estado calculado al construir ProductResponse: `currentStock < criticalThreshold` → "CRITICO", si no → "OK"
- `getAlerts()`: productos activos donde currentStock < criticalThreshold

**`ProductController`**
- `GET /api/products` — todos los roles (query params: categoryId, status, name)
- `POST /api/products` — solo ADMIN
- `PUT /api/products/{id}` — solo ADMIN
- `PATCH /api/products/{id}/deactivate` — solo ADMIN
- `GET /api/products/alerts` — todos los roles

---

## Fase 9: Módulo Movimientos

**`MovementService`** (interfaz)
- `register(MovementRequest, String userId, String userName) → MovementResponse`
- `findAll(LocalDateTime from, LocalDateTime to, MovementType type, String productId) → List<MovementResponse>`

**`MovementServiceImpl`**
- Para SALIDA: verificar `product.currentStock >= quantity`, lanzar excepcion con mensaje claro si no hay stock
- Guardar snapshot: copiar productName y categoryName del producto en el momento del registro
- Actualizar `currentStock` del producto: sumar en ENTRADA, restar en SALIDA
- `timestamp` se asigna automaticamente con `LocalDateTime.now()`

**`MovementController`**
- `POST /api/movements` — todos los roles (userId y userName se extraen del token JWT)
- `GET /api/movements` — todos los roles (query params: from, to, type, productId)

---

## Fase 10: Módulo Usuarios

**`UserService`** (interfaz)
- `findAll() → List<UserResponse>`
- `create(UserRequest) → UserResponse`
- `update(String id, UserRequest) → UserResponse`
- `deactivate(String id)`

**`UserServiceImpl`**
- Hashear password con BCrypt al crear y al actualizar
- Nunca incluir passwordHash en UserResponse

**`UserController`** — todos los endpoints requieren rol ADMIN
- `GET /api/users`
- `POST /api/users`
- `PUT /api/users/{id}`
- `PATCH /api/users/{id}/deactivate`

---

## Fase 11: Módulo Reportes y Exportaciones

**`ReportService`** (interfaz)
- `getMonthlyReport(int year, int month) → List<MonthlyReportResponse>`
- `exportInventoryExcel() → byte[]`
- `exportMonthlyExcel(int year, int month) → byte[]`

**`ReportServiceImpl`**
- `getMonthlyReport`: filtrar movimientos por rango de fechas del mes y tipo SALIDA, agrupar por categoryName, sumar quantities
- `exportInventoryExcel`: generar Excel con POI — columnas: nombre, categoría, talla, stock actual, estado, umbral crítico
- `exportMonthlyExcel`: generar Excel con POI — columnas: categoría, total unidades donadas

**`ReportController`**
- `GET /api/reports/monthly?year=&month=` — todos los roles
- `GET /api/exports/inventory` — respuesta con header `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- `GET /api/exports/monthly?year=&month=` — mismo Content-Type

---

## Fase 12: Frontend React

**12.1 Crear proyecto**
```bash
npm create vite@latest gestion-social-frontend -- --template react
cd gestion-social-frontend
npm install @mui/material @emotion/react @emotion/styled
npm install react-router-dom axios
```

**12.2 `src/theme.js`**
- Personalizar paleta MUI con colores institucionales (azul/verde de alcaldia)

**12.3 `src/api/client.js`**
- Instancia de axios con `baseURL` apuntando al backend en Railway
- Interceptor que agrega header `Authorization: Bearer <token>` en cada request
- Interceptor de respuesta que redirige al login si recibe 401

**12.4 `src/context/AuthContext.jsx`**
- Estado: `user` (null si no autenticado), `token`
- Funciones: `login(email, password)`, `logout()`
- Al iniciar: leer token de localStorage y decodificar payload para recuperar sesion

**12.5 `src/App.jsx`**
- Configurar React Router con todas las rutas
- Componente `PrivateRoute`: si no hay token → redirige a login; si rol no es ADMIN en ruta de admin → redirige a dashboard

**12.6 Paginas** (una por modulo, usando componentes MUI):
- `Login.jsx` — formulario con TextField, Button, manejo de error
- `Dashboard.jsx` — cards de alertas criticas con color rojo
- `Inventory.jsx` — DataGrid o Table con filtros Select y TextField
- `Movements.jsx` — Autocomplete para selector de producto, formulario de registro
- `History.jsx` — tabla con filtros de fecha (DatePicker), tipo y producto
- `Reports.jsx` — tabla de totales por categoria, botones de descarga Excel
- `Products.jsx` — tabla + modal para crear/editar producto
- `Categories.jsx` — tabla + modal para crear/editar categoria
- `Users.jsx` — tabla + modal para crear/editar usuario

**12.7 `src/components/Navbar.jsx`**
- AppBar de MUI con enlaces a todas las paginas
- Ocultar enlaces de admin si `user.role !== 'ADMIN'`
- Boton de logout

---

## Orden de Implementación

| # | Tarea | Depende de |
|---|-------|-----------|
| 1 | pom.xml + application.properties | — |
| 2 | Modelos + enums | — |
| 3 | Repositorios | Modelos |
| 4 | JWT (JwtUtil + JwtAuthFilter) | — |
| 5 | SecurityConfig + DataInitializer | JWT |
| 6 | DTOs | Modelos |
| 7 | Modulo Auth | Todo lo anterior |
| 8 | Modulo Categorias | Auth funcionando |
| 9 | Modulo Productos | Categorias |
| 10 | Modulo Movimientos | Productos |
| 11 | Modulo Usuarios | Auth |
| 12 | Modulo Reportes | Movimientos |
| 13 | Frontend React | Backend completo |

---

## Variables de Entorno

**Railway (backend):**
```
MONGODB_URI=mongodb+srv://...
JWT_SECRET=clave-secreta-minimo-32-caracteres
ADMIN_EMAIL=admin@bodega.gov.co
ADMIN_PASSWORD=contraseña-segura
FRONTEND_URL=https://tu-app.netlify.app
```

**Netlify (frontend):**
```
VITE_API_URL=https://tu-backend.railway.app
```
