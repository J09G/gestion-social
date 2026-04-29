# Diseño: Sistema de Gestión de Bodega Social

**Fecha:** 2026-04-29  
**Stack:** Spring Boot (Java) + MongoDB + HTML/CSS/JS vanilla + JWT  
**Hosting:** Railway (backend) + MongoDB Atlas (base de datos)

---

## 1. Contexto del Negocio

Bodega social de la alcaldía que recibe productos donados y los distribuye a familias, niños, estudiantes y personas de bajos recursos. Maneja 6 categorías de productos: Juguetes, Útiles Escolares, Calzado, Ropa, Tulas, Hogar.

Solo las categorías Calzado y Ropa manejan talla. El resto no tiene atributo de talla.

---

## 2. Roles y Permisos

| Funcionalidad             | ADMIN | BODEGUERO |
|---------------------------|-------|-----------|
| Login                     | si    | si        |
| Ver inventario            | si    | si        |
| Registrar movimiento      | si    | si        |
| Ver historial             | si    | si        |
| Ver reportes              | si    | si        |
| Exportar Excel            | si    | si        |
| Gestionar categorías      | si    | si        |
| Gestionar productos       | si    | no        |
| Gestionar usuarios        | si    | no        |

---

## 3. Modelo de Datos (MongoDB)

### Colección `users`
```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "passwordHash": "string",
  "role": "ADMIN | BODEGUERO",
  "active": "boolean",
  "createdAt": "datetime"
}
```

### Colección `categories`
```json
{
  "id": "string",
  "name": "string"
}
```
Valores iniciales: Juguetes, Útiles Escolares, Calzado, Ropa, Tulas, Hogar. Gestionables desde la interfaz.

### Colección `products`
```json
{
  "id": "string",
  "name": "string",
  "categoryId": "string",
  "hasSize": "boolean",
  "size": "string | null",
  "currentStock": "int",
  "criticalThreshold": "int",
  "active": "boolean",
  "createdAt": "datetime"
}
```
`hasSize` es true solo para productos de Calzado y Ropa. El campo `size` es texto libre que representa un rango (ej: "36 - 40", "S - XL", "Talla única"). No se crean documentos separados por talla; cada producto tiene un rango y un stock total para ese rango. `criticalThreshold` es configurado por el admin por producto.

### Colección `movements`
```json
{
  "id": "string",
  "productId": "string",
  "productName": "string (snapshot)",
  "categoryName": "string (snapshot)",
  "type": "ENTRADA | SALIDA",
  "quantity": "int",
  "userId": "string",
  "userName": "string (snapshot)",
  "timestamp": "datetime"
}
```
Los campos snapshot (productName, categoryName, userName) se copian en el momento del registro para preservar el historial aunque luego se editen esos documentos. El historial es inmutable: no existe endpoint de DELETE ni UPDATE para movimientos.

---

## 4. Lógica de Negocio

- **Stock:** `currentStock` en `products` se actualiza atómicamente con cada movimiento.
- **Validación de salida:** antes de registrar una SALIDA se verifica que `currentStock >= quantity`. Si no hay stock suficiente, se rechaza con error.
- **Estado CRÍTICO:** si `currentStock < criticalThreshold`, el producto aparece con estado CRÍTICO en el inventario.
- **Alertas:** al entrar al dashboard se muestran todos los productos con estado CRÍTICO.
- **Auditoría:** cada movimiento registra `userId` y `userName` del usuario autenticado, obtenido del token JWT.

---

## 5. API REST

Todos los endpoints bajo `/api/**`. JWT requerido en todos excepto login.

### Autenticación
```
POST   /api/auth/login
```

### Productos
```
GET    /api/products                        filtros: categoria, estado, nombre
POST   /api/products                        admin
PUT    /api/products/{id}                   admin
PATCH  /api/products/{id}/deactivate        admin
GET    /api/products/alerts                 productos en estado CRITICO
```

### Categorías
```
GET    /api/categories
POST   /api/categories                      admin + bodeguero
PUT    /api/categories/{id}                 admin + bodeguero
```

### Movimientos
```
POST   /api/movements
GET    /api/movements                       filtros: fecha, tipo, productId
```
El formulario de registro de movimientos usa un selector con búsqueda para elegir el producto existente (nombre + categoría + talla). El usuario no escribe el nombre a mano. El request solo envía `productId`, `type` y `quantity`.

### Usuarios
```
GET    /api/users                           admin
POST   /api/users                           admin
PUT    /api/users/{id}                      admin
PATCH  /api/users/{id}/deactivate           admin
```

### Reportes y Exportaciones
```
GET    /api/reports/monthly?year=&month=    totales de salidas por categoría
GET    /api/exports/inventory               inventario actual en Excel
GET    /api/exports/monthly?year=&month=    reporte mensual en Excel
```

---

## 6. Seguridad

- JWT firmado con clave secreta, expiración 8 horas.
- Frontend guarda token en `localStorage`, lo envía como `Authorization: Bearer <token>`.
- Al expirar el token, el frontend redirige al login.
- Spring Security protege endpoints por rol.
- **Usuario admin inicial:** al arrancar la app, si no existe ningún admin en la BD, `DataInitializer` crea uno con credenciales configurables via variables de entorno en Railway.

---

## 7. Estructura del Proyecto Spring Boot

```
src/main/java/com/bodega/gestion_social/
├── config/
│   ├── SecurityConfig.java
│   └── DataInitializer.java
├── controller/
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CategoryController.java
│   ├── MovementController.java
│   ├── UserController.java
│   └── ReportController.java
├── service/
│   ├── AuthService.java          (interfaz)
│   ├── ProductService.java       (interfaz)
│   ├── CategoryService.java      (interfaz)
│   ├── MovementService.java      (interfaz)
│   ├── UserService.java          (interfaz)
│   ├── ReportService.java        (interfaz)
│   └── impl/
│       ├── AuthServiceImpl.java
│       ├── ProductServiceImpl.java
│       ├── CategoryServiceImpl.java
│       ├── MovementServiceImpl.java
│       ├── UserServiceImpl.java
│       └── ReportServiceImpl.java
├── repository/
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CategoryRepository.java
│   └── MovementRepository.java
├── model/
│   ├── User.java
│   ├── Product.java
│   ├── Category.java
│   └── Movement.java
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── ProductRequest.java
│   │   ├── MovementRequest.java
│   │   ├── UserRequest.java
│   │   └── CategoryRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── ProductResponse.java
│       ├── MovementResponse.java
│       └── MonthlyReportResponse.java
├── security/
│   ├── JwtUtil.java
│   └── JwtAuthFilter.java
└── GestionSocialApplication.java
```

**Dependencias principales:**
- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-mongodb`
- `jjwt` (JWT)
- `apache-poi` (Excel)

---

## 8. Estructura del Frontend

Archivos estáticos en `src/main/resources/static/`:

```
├── login.html
├── dashboard.html          alertas de stock crítico + accesos rápidos
├── inventory.html          tabla con filtros por categoría, estado y nombre
├── movements.html          formulario registrar entrada/salida
├── history.html            historial con filtros por fecha, tipo y producto
├── reports.html            reporte mensual por categoría + exportar Excel
├── products.html           gestión de productos (admin)
├── categories.html         gestión de categorías (admin + bodeguero)
├── users.html              gestión de usuarios (admin)
├── css/
│   └── styles.css
└── js/
    ├── auth.js             verifica token en cada página, maneja logout
    ├── api.js              funciones fetch con header Authorization incluido
    ├── dashboard.js
    ├── inventory.js
    ├── movements.js
    ├── history.js
    ├── reports.js
    ├── products.js
    ├── categories.js
    └── users.js
```

Cada `.js` maneja exclusivamente su `.html` correspondiente. La navegación común se inyecta via JS. Los enlaces de admin (productos, usuarios) se muestran solo si el rol en el token es ADMIN.

---

## 9. Exportación Excel

Generado con Apache POI en el backend.

- **Inventario actual:** columnas nombre, categoría, talla, stock actual, estado, umbral crítico.
- **Reporte mensual:** columnas categoría, total unidades donadas en el mes.

El archivo se descarga directamente desde el navegador al hacer clic en el botón de exportar.
