# TP5 — Proyecto Grupal Integrador: Gestión de Inventario Inteligente

---

**Modalidad:** Grupal (3-4 integrantes)  
**Duración:** 4 semanas  
**Stack:** Java 21 · Spring Boot 3.5.x · Maven · In-Memory Storage · Swagger/OpenAPI

---

## 1. Objetivos

Al finalizar este proyecto integrador, cada grupo será capaz de:

1. **Diseñar e implementar un microservicio REST completo** con arquitectura en capas estricta (Controller → Service → Repository), respetando el principio de responsabilidad única en cada estrato.
2. **Aplicar genéricos de Java** mediante la definición de `IGenericRepository<T, ID>` y una implementación abstracta reutilizable `GenericInMemoryRepository<T, ID>`, demostrando el poder del polimorfismo paramétrico.
3. **Implementar el patrón Strategy** para reglas de alerta de stock intercambiables en tiempo de configuración, permitiendo modificar el comportamiento del sistema sin alterar su código fuente (OCP).
4. **Usar composición sobre herencia** en el modelo de dominio: un `Producto` **tiene una** `Categoria`, en lugar de heredar de ella.
5. **Documentar exhaustivamente con JavaDoc** todo el código fuente, alcanzando el 100% de cobertura en clases públicas, interfaces y métodos públicos.
6. **Producir un Performance Report** que justifique teóricamente la complejidad Big O de cada endpoint y la valide empíricamente con mediciones de `System.nanoTime()` para 1k, 10k y 100k registros.
7. **Aplicar los cinco principios SOLID completos** en cada capa del sistema, verificables mediante inspección de código.
8. **Configurar springdoc-openapi** para generar documentación interactiva Swagger/OpenAPI accesible desde el navegador.

---

## 2. Descripción del Sistema

Se requiere desarrollar un **microservicio REST para la gestión de inventario de un depósito inteligente**. El sistema debe permitir:

- **Administrar productos:** altas, bajas, modificaciones y consultas del catálogo. Cada producto pertenece a una categoría, tiene un precio unitario y una cantidad en stock.
- **Administrar categorías:** CRUD completo de las categorías que agrupan productos (ej.: Electrónicos, Alimentos, Limpieza).
- **Registrar movimientos de inventario:** entradas (incrementos de stock) y salidas (decrementos de stock), manteniendo un historial trazable de cada operación.
- **Generar alertas automáticas:** cuando el stock de un producto desciende por debajo de un umbral configurable, el sistema debe exponer endpoints para consultar productos en estado de alerta.
- **Emitir reportes de performance:** un endpoint administrativo que genere un reporte JSON con las complejidades algorítmicas y los tiempos de ejecución reales de cada operación.

### Reglas de negocio fundamentales

1. No se puede retirar más stock del existente. Una solicitud de retiro que exceda el stock disponible debe resultar en un error HTTP **409 Conflict**.
2. Cada movimiento de inventario (entrada o salida) modifica el stock del producto de forma **atómica** utilizando `AtomicInteger` (thread-safe).
3. Un producto se considera en **alerta** cuando `stock < stockMinimo`. Si `stock < stockCritico`, la alerta es de nivel **crítico**. Ambos umbrales son configurables en `application.yml`.
4. Al crear un producto, el stock inicial debe ser mayor o igual a **0**.
5. No se puede eliminar una categoría que tenga productos asociados. El sistema debe retornar un error **409 Conflict** con un mensaje descriptivo.

---

## 3. Arquitectura

### 3.1 Diagrama de capas

```
┌──────────────────────────────────────────────────────────────────────────┐
│                          CLIENTE HTTP                                     │
│                    (curl, Postman, Swagger UI)                            │
└────────────────────────────────┬─────────────────────────────────────────┘
                                 │ REST/JSON
                                 ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                       CONTROLLERS (@RestController)                      │
│  ┌─────────────────┐ ┌──────────────────┐ ┌──────────────────┐ ┌───────┐│
│  │ProductoController│ │CategoriaController│ │MovimientoController│ │AlertaC││
│  └────────┬────────┘ └────────┬─────────┘ └────────┬─────────┘ └───┬───┘│
│           │                   │                    │               │    │
│   Validación @Valid   Delegación pura a servicios  │               │    │
│   Conversión DTO ←→ Modelo                         │               │    │
└───────────┼───────────────────┼────────────────────┼───────────────┼────┘
            │                   │                    │               │
            ▼                   ▼                    ▼               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        SERVICES (@Service)                                │
│  ┌─────────────────┐ ┌──────────────────┐ ┌──────────────────┐ ┌───────┐│
│  │ProductoService  │ │CategoriaService  │ │MovimientoService │ │AlertaS││
│  │   Impl          │ │     Impl         │ │      Impl        │ │ Impl  ││
│  └────────┬────────┘ └────────┬─────────┘ └────────┬─────────┘ └───┬───┘│
│           │                   │                    │               │    │
│   Reglas de negocio    Validaciones      AtomicInteger          Strategy│
│   Composición vs       Reglas de         actualizaciones        Pattern │
│   herencia             integridad        de stock                      │
└───────────┼───────────────────┼────────────────────┼───────────────┼────┘
            │                   │                    │               │
            ▼                   ▼                    ▼               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                     REPOSITORIES (@Repository)                            │
│  ┌──────────────────────────────────────────────────────────────────────┐│
│  │            IGenericRepository<T, ID>  (interface genérica)            ││
│  │  findAll() : List<T>                                                  ││
│  │  findById(ID) : Optional<T>                                          ││
│  │  save(T) : T                                                          ││
│  │  deleteById(ID) : void                                                ││
│  │  existsById(ID) : boolean                                             ││
│  └──────────────────────────────────────────────────────────────────────┘│
│                                    ▲                                      │
│                                    │ implements                           │
│  ┌──────────────────────────────────────────────────────────────────────┐│
│  │      GenericInMemoryRepository<T, ID> (clase abstracta)              ││
│  │      protected final ConcurrentHashMap<ID, T> dataStore;             ││
│  │      protected final AtomicLong idGenerator;                         ││
│  └──────────────────────────────────────────────────────────────────────┘│
│                    ▲                          ▲                           │
│                    │ extends                  │ extends                   │
│  ┌─────────────────┴──────────┐ ┌─────────────┴──────────────┐           │
│  │  InMemoryProductoRepository│ │  InMemoryCategoriaRepository│           │
│  │  + findByCategoria(Long)   │ │  (hereda comportamiento     │           │
│  │  + buscarPorNombre(String) │ │   base, sin queries extra)  │           │
│  └────────────────────────────┘ └────────────────────────────┘           │
└──────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Flujo de una petición típica

```
POST /api/productos  →  ProductoController.crear(ProductoRequest)
                           →  ProductoServiceImpl.crear(ProductoRequest)
                                →  ProductoRepository.save(producto)
                                     →  ConcurrentHashMap.put(id, producto)
                           ←  ProductoResponse (Record inmutable)
                     ←  201 Created + Location header
```

---

## 4. Estructura del Proyecto

```
tp5-inventario/
├── pom.xml
├── README.md
├── PERFORMANCE.md
└── src/
    ├── main/java/com/inventory/smart/
    │   ├── SmartInventoryApplication.java
    │   ├── model/
    │   │   ├── Producto.java
    │   │   ├── Categoria.java
    │   │   ├── MovimientoInventario.java
    │   │   ├── TipoMovimiento.java
    │   │   └── NivelAlerta.java
    │   ├── dto/
    │   │   ├── ProductoRequest.java
    │   │   ├── ProductoResponse.java
    │   │   ├── CategoriaRequest.java
    │   │   ├── CategoriaResponse.java
    │   │   ├── MovimientoRequest.java
    │   │   ├── MovimientoResponse.java
    │   │   └── AlertaStockResponse.java
    │   ├── repository/
    │   │   ├── IGenericRepository.java
    │   │   ├── GenericInMemoryRepository.java
    │   │   ├── ProductoRepository.java
    │   │   ├── CategoriaRepository.java
    │   │   ├── MovimientoRepository.java
    │   │   └── InMemoryProductoRepository.java
    │   ├── service/
    │   │   ├── ProductoService.java
    │   │   ├── ProductoServiceImpl.java
    │   │   ├── CategoriaService.java
    │   │   ├── CategoriaServiceImpl.java
    │   │   ├── MovimientoService.java
    │   │   ├── MovimientoServiceImpl.java
    │   │   ├── AlertaService.java
    │   │   ├── AlertaServiceImpl.java
    │   │   └── PerformanceReportService.java
    │   ├── controller/
    │   │   ├── ProductoController.java
    │   │   ├── CategoriaController.java
    │   │   ├── MovimientoController.java
    │   │   └── AlertaController.java
    │   ├── exception/
    │   │   ├── ResourceNotFoundException.java
    │   │   ├── InsufficientStockException.java
    │   │   ├── BusinessRuleException.java
    │   │   └── GlobalExceptionHandler.java
    │   └── config/
    │       ├── StockConfig.java
    │       └── DataInitializer.java
    ├── main/resources/
    │   └── application.yml
    └── test/java/com/inventory/smart/
        └── (tests a implementar por los alumnos)
```

---

## 5. API Endpoints

### 5.1 Tabla completa de endpoints

| Método | Endpoint | Big O esperado | Descripción |
|--------|----------|:---:|-------------|
| `GET` | `/api/productos` | O(n) | Listar todos los productos. Soporta filtros opcionales por query params: `categoria`, `precioMin`, `precioMax`, `enStock` (boolean). |
| `GET` | `/api/productos/{id}` | O(1) | Obtener un producto por su identificador único. Retorna 404 si no existe. |
| `POST` | `/api/productos` | O(1) | Crear un nuevo producto. El body debe ser JSON válido con `@Valid`. Stock inicial ≥ 0. Retorna 201 Created con header `Location`. |
| `PUT` | `/api/productos/{id}` | O(1) | Actualizar un producto existente. Si no existe, retorna 404. Los campos no enviados mantienen su valor actual. |
| `DELETE` | `/api/productos/{id}` | O(1) | Eliminar un producto. Retorna 204 No Content si existe, 404 si no. |
| `GET` | `/api/productos/buscar?q=` | O(n) | Búsqueda textual por nombre (contiene, case-insensitive). Si `q` está vacío, retorna 400 Bad Request. |
| `GET` | `/api/productos/ordenados?campo=&orden=` | O(n log n) | Listar productos ordenados por un campo (`nombre`, `precio`, `stock`) en orden `asc` o `desc`. Usa `List.sort()` con `Comparator`. |
| `GET` | `/api/categorias` | O(n) | Listar todas las categorías. |
| `GET` | `/api/categorias/{id}` | O(1) | Obtener una categoría por ID. |
| `POST` | `/api/categorias` | O(1) | Crear una categoría. |
| `PUT` | `/api/categorias/{id}` | O(1) | Actualizar una categoría. |
| `DELETE` | `/api/categorias/{id}` | O(1) | Eliminar categoría. Retorna **409 Conflict** si tiene productos asociados. |
| `POST` | `/api/movimientos` | O(1) | Registrar un movimiento de inventario (ENTRADA o SALIDA). Si es SALIDA y el stock es insuficiente, retorna **409 Conflict**. |
| `GET` | `/api/movimientos/producto/{id}` | O(n) | Obtener el historial de movimientos de un producto específico. |
| `GET` | `/api/alertas/stock-bajo` | O(n) | Listar productos cuyo stock está por debajo del umbral mínimo configurado. Incluye el nivel de alerta (BAJO o CRITICO). |
| `GET` | `/api/admin/performance-report` | — | Generar y retornar un reporte JSON con los tiempos de ejecución medidos de cada operación para 1k, 10k y 100k registros. |

### 5.2 Ejemplos de requests y responses

#### Crear un producto

```http
POST /api/productos HTTP/1.1
Content-Type: application/json

{
    "nombre": "Notebook Dell XPS 15",
    "descripcion": "Laptop de alto rendimiento",
    "precio": 1599.99,
    "stockInicial": 25,
    "categoriaId": 1
}
```

```http
HTTP/1.1 201 Created
Location: /api/productos/42
Content-Type: application/json

{
    "id": 42,
    "nombre": "Notebook Dell XPS 15",
    "descripcion": "Laptop de alto rendimiento",
    "precio": 1599.99,
    "stock": 25,
    "categoria": {
        "id": 1,
        "nombre": "Electrónicos"
    }
}
```

#### Registrar una salida de stock

```http
POST /api/movimientos HTTP/1.1
Content-Type: application/json

{
    "productoId": 42,
    "tipo": "SALIDA",
    "cantidad": 5,
    "motivo": "Venta al cliente #1083"
}
```

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
    "id": 157,
    "productoId": 42,
    "tipo": "SALIDA",
    "cantidad": 5,
    "stockResultante": 20,
    "motivo": "Venta al cliente #1083",
    "fecha": "2026-06-03T14:30:00"
}
```

#### Intentar retirar más stock del existente

```http
POST /api/movimientos HTTP/1.1
Content-Type: application/json

{
    "productoId": 42,
    "tipo": "SALIDA",
    "cantidad": 100,
    "motivo": "Pedido mayorista"
}
```

```http
HTTP/1.1 409 Conflict
Content-Type: application/json

{
    "error": "Stock insuficiente",
    "mensaje": "No se pueden retirar 100 unidades. Stock disponible: 20",
    "productoId": 42,
    "stockDisponible": 20,
    "timestamp": "2026-06-03T14:31:00"
}
```

---

## 6. Consignas Técnicas

### 6.1 Interfaces Genéricas (30%)

#### 6.1.1 Definición de `IGenericRepository<T, ID>`

El grupo debe definir una **interfaz genérica** que establezca el contrato para todos los repositorios del sistema:

```java
package com.inventory.smart.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para repositorios que define las operaciones CRUD básicas.
 *
 * @param <T>  tipo de la entidad gestionada
 * @param <ID> tipo del identificador de la entidad
 * @since 1.0
 */
public interface IGenericRepository<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T entity);

    void deleteById(ID id);

    boolean existsById(ID id);

    long count();
}
```

#### 6.1.2 Implementación abstracta `GenericInMemoryRepository<T, ID>`

Esta clase abstracta implementa `IGenericRepository` utilizando `ConcurrentHashMap<ID, T>` como almacenamiento subyacente. Debe ser **reutilizable por cualquier repositorio concreto** sin duplicar código.

**Requerimientos técnicos:**

- El mapa interno debe ser `protected final ConcurrentHashMap<ID, T> dataStore`.
- El generador de IDs debe ser `protected final AtomicLong idGenerator` (iniciado en 1).
- `save(T entity)`: si la entidad no tiene ID asignado, le asigna uno usando `idGenerator.incrementAndGet()`. Si ya tiene ID, actualiza la entrada existente.
- `deleteById(ID id)`: elimina la entrada; si no existe, lanza `ResourceNotFoundException`.
- **¿Por qué `ConcurrentHashMap`?** Porque es thread-safe sin bloquear todo el mapa (segmentación de locks en Java 8+, lock stripping). Si se usara `HashMap`, accesos concurrentes desde múltiples requests HTTP podrían causar condiciones de carrera o `ConcurrentModificationException`.

#### 6.1.3 Repositorios concretos

Los repositorios concretos **extienden** la clase abstracta y pueden agregar queries específicas:

```java
@Repository
public class InMemoryProductoRepository
        extends GenericInMemoryRepository<Producto, Long>
        implements ProductoRepository {

    @Override
    public List<Producto> findByCategoria(Long categoriaId) {
        return dataStore.values().stream()
                .filter(p -> p.getCategoria().getId().equals(categoriaId))
                .toList();
    }

    @Override
    public List<Producto> buscarPorNombre(String texto) {
        String lower = texto.toLowerCase();
        return dataStore.values().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(lower))
                .toList();
    }
}
```

**Puntos a justificar:**

- La interfaz `ProductoRepository` **extiende** `IGenericRepository<Producto, Long>` (ISP — Interface Segregation).
- `InMemoryProductoRepository` **extiende** `GenericInMemoryRepository<Producto, Long>` para heredar el comportamiento CRUD base.
- Las queries específicas usan `Stream.filter()` sobre `dataStore.values()`, que itera todos los elementos → `O(n)`.

---

### 6.2 Arquitectura en Capas (25%)

#### 6.2.1 Responsabilidades de cada capa

| Capa | Responsabilidad | NO debe |
|------|----------------|---------|
| **Controller** | Recibir requests HTTP, validar inputs con `@Valid`, delegar a servicios, convertir entidades a DTOs, retornar `ResponseEntity`. | Contener lógica de negocio, acceder directamente a repositorios, retornar entidades del modelo. |
| **Service** | Orquestar reglas de negocio, coordinar múltiples repositorios, lanzar excepciones de negocio, gestionar transaccionalidad lógica. | Acceder a detalles HTTP (`HttpServletRequest`), retornar `ResponseEntity`, conocer nombres de endpoints. |
| **Repository** | Almacenar y recuperar entidades, ejecutar queries. | Contener reglas de negocio, validar inputs de usuario, conocer la capa HTTP. |

#### 6.2.2 Reglas obligatorias

1. **PROHIBIDO retornar entidades del modelo desde controllers.** Todos los responses deben ser Records DTO (`ProductoResponse`, `CategoriaResponse`, etc.). La conversión Entity → DTO ocurre en el controller o en un mapper explícito dentro del service.

2. **Validaciones con `@Valid` en los Requests.** Todos los DTOs de entrada deben estar anotados con Jakarta Bean Validation:
   ```java
   public record ProductoRequest(
       @NotBlank(message = "El nombre es obligatorio")
       @Size(min = 2, max = 100)
       String nombre,

       @Size(max = 500)
       String descripcion,

       @PositiveOrZero(message = "El precio debe ser >= 0")
       double precio,

       @PositiveOrZero(message = "El stock inicial debe ser >= 0")
       int stockInicial,

       @NotNull(message = "La categoría es obligatoria")
       Long categoriaId
   ) {}
   ```

3. **Inyección por constructor** en TODOS los componentes:
   ```java
   @RestController
   @RequestMapping("/api/productos")
   public class ProductoController {
       private final ProductoService productoService;

       // Constructor injection — sin @Autowired
       public ProductoController(ProductoService productoService) {
           this.productoService = productoService;
       }
   }
   ```

4. **Manejo centralizado de excepciones** con `@ControllerAdvice`:
   ```java
   @ControllerAdvice
   public class GlobalExceptionHandler {
       @ExceptionHandler(ResourceNotFoundException.class)
       public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(...);
       }
       // handlers para InsufficientStockException, BusinessRuleException, MethodArgumentNotValidException
   }
   ```

---

### 6.3 Reglas de Negocio (15%)

#### 6.3.1 Atomicidad del stock con `AtomicInteger`

El campo `stock` en la clase `Producto` debe ser de tipo `AtomicInteger`:

```java
public class Producto {
    private final Long id;
    private final String nombre;
    // ... otros campos final ...
    private final AtomicInteger stock;  // thread-safe

    public int getStock() {
        return stock.get();
    }

    public int incrementarStock(int cantidad) {
        return stock.addAndGet(cantidad);
    }

    public int decrementarStock(int cantidad) {
        return stock.addAndGet(-cantidad);
    }
}
```

Esto garantiza que múltiples requests concurrentes que modifiquen el stock del mismo producto no generen condiciones de carrera.

#### 6.3.2 Excepciones de negocio

| Excepción | HTTP Status | Gatillador |
|-----------|:---:|-----------|
| `ResourceNotFoundException` | 404 | Entidad no encontrada por ID. |
| `InsufficientStockException` | 409 | Intento de retirar más stock del disponible. |
| `BusinessRuleException` | 409 | Eliminar categoría con productos asociados; crear producto con stock negativo. |
| `MethodArgumentNotValidException` | 400 | Validaciones de Jakarta Bean Validation fallidas (manejada por Spring). |

#### 6.3.3 Umbrales de alerta configurables

```yaml
# application.yml
inventario:
  stock:
    minimo: 10    # Por debajo → alerta BAJO
    critico: 3    # Por debajo → alerta CRITICO
```

El `AlertaService` debe leer estos valores mediante `@ConfigurationProperties`:

```java
@ConfigurationProperties(prefix = "inventario.stock")
public record StockConfig(int minimo, int critico) {}
```

#### 6.3.4 Niveles de alerta

```java
public enum NivelAlerta {
    NORMAL,    // stock >= minimo
    BAJO,      // critico <= stock < minimo
    CRITICO    // stock < critico
}
```

---

### 6.4 Documentación JavaDoc (15%)

#### Requisitos estrictos

1. **100% de clases públicas e interfaces** deben tener JavaDoc con:
   - Descripción del propósito de la clase.
   - `@author` (nombre del grupo o integrantes).
   - `@since "1.0"`.

2. **100% de métodos públicos** deben tener:
   - `@param` para cada parámetro.
   - `@return` (si no es `void`).
   - `@throws` para cada excepción declarada o documentada.
   - Descripción clara de lo que hace el método.

3. **El proyecto debe compilar sin warnings** al ejecutar:
   ```bash
   mvn javadoc:javadoc
   ```

#### Ejemplo de JavaDoc correcto

```java
/**
 * Servicio encargado de la gestión de productos del inventario.
 *
 * <p>Proporciona operaciones CRUD, búsqueda textual y ordenamiento
 * parametrizado de productos. Aplica las reglas de negocio definidas
 * para la creación y modificación de productos.</p>
 *
 * @author Grupo 3 — Inventario Inteligente
 * @since 1.0
 */
public interface ProductoService {

    /**
     * Busca un producto por su identificador único.
     *
     * @param id identificador del producto (no nulo, positivo)
     * @return el producto encontrado
     * @throws ResourceNotFoundException si no existe producto con el ID dado
     * @since 1.0
     */
    Producto findById(Long id);
}
```

---

## 7. Performance Report

Los alumnos deben crear un archivo `PERFORMANCE.md` en la raíz del proyecto que contenga:

### 7.1 Tabla de complejidad

| Endpoint | Operación dominante | Big O teórico | Justificación |
|----------|---------------------|:---:|---------------|
| `GET /api/productos` | `Stream.filter()` sobre `ConcurrentHashMap.values()` | O(n) | Itera todos los elementos para aplicar filtros. |
| `GET /api/productos/{id}` | `ConcurrentHashMap.get(key)` | O(1) | Hash table con función de dispersión uniforme. Amortizado O(1). |
| `POST /api/productos` | `ConcurrentHashMap.put(key, value)` | O(1) | Inserción en hash table. Amortizado O(1). |
| `PUT /api/productos/{id}` | `ConcurrentHashMap.put(key, value)` | O(1) | Reemplazo en hash table. Amortizado O(1). |
| `DELETE /api/productos/{id}` | `ConcurrentHashMap.remove(key)` | O(1) | Eliminación en hash table. Amortizado O(1). |
| `GET /api/productos/buscar?q=` | `Stream.filter()` + `String.contains()` | O(n·m) | Itera n productos; `contains()` es O(m) donde m es longitud del texto de búsqueda. |
| `GET /api/productos/ordenados` | `List.sort()` (TimSort) | O(n log n) | TimSort es O(n log n) en caso promedio y peor caso. |
| `POST /api/movimientos` | `ConcurrentHashMap.put()` + `AtomicInteger.addAndGet()` | O(1) | Ambas operaciones son O(1). |
| `GET /api/movimientos/producto/{id}` | `Stream.filter()` sobre lista de movimientos | O(n) | Itera todos los movimientos para filtrar por producto. |
| `GET /api/alertas/stock-bajo` | `Stream.filter()` sobre `values()` | O(n) | Itera todos los productos evaluando condición de stock. |

### 7.2 Tabla de mediciones

Los alumnos deben completar esta tabla ejecutando `GET /api/admin/performance-report` con datasets de 1k, 10k y 100k registros:

| Endpoint | 1k registros | 10k registros | 100k registros | Escala observada |
|----------|:---:|:---:|:---:|:---:|
| `GET /api/productos` | ___ ns | ___ ns | ___ ns | ___ |
| `GET /api/productos/{id}` | ___ ns | ___ ns | ___ ns | ___ |
| `POST /api/productos` | ___ ns | ___ ns | ___ ns | ___ |
| `GET /api/productos/buscar?q=` | ___ ns | ___ ns | ___ ns | ___ |
| `GET /api/productos/ordenados` | ___ ns | ___ ns | ___ ns | ___ |
| `GET /api/alertas/stock-bajo` | ___ ns | ___ ns | ___ ns | ___ |

### 7.3 Justificación de discrepancias

Los alumnos deben explicar cualquier discrepancia entre la complejidad teórica y la observada. Por ejemplo:
- El overhead de `Stream` y las lambdas puede hacer que operaciones O(1) parezcan más lentas que O(n) para conjuntos de datos pequeños.
- `String.contains()` en búsqueda textual introduce un factor adicional O(m) no siempre visible.
- La recolección de basura (GC) puede introducir ruido en las mediciones.

---

## 8. Configuración `application.yml`

```yaml
server:
  port: 8081

spring:
  application:
    name: smart-inventory

# Documentación OpenAPI / Swagger
springdoc:
  api-docs:
    path: /api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operationsSorter: method
    tagsSorter: alpha
  show-actuator: false

# Configuración de inventario
inventario:
  stock:
    minimo: 10
    critico: 3

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized
```

---

## 9. Criterios de Evaluación

| Criterio | Peso | Descripción |
|----------|:---:|-------------|
| **Interfaces Genéricas** | 20% | Correcta definición de `IGenericRepository<T, ID>`. Implementación abstracta `GenericInMemoryRepository<T, ID>` reutilizable y bien diseñada. Uso correcto de `ConcurrentHashMap` y justificación de thread-safety. |
| **SOLID & Arquitectura** | 20% | Separación estricta de capas (Controller → Service → Repository). DIP: dependencias inyectadas por constructor, cero `@Autowired` en campos. SRP: cada clase tiene una única responsabilidad. OCP: patrón Strategy en alertas, filtros extensibles. |
| **Funcionalidad completa** | 15% | Todos los endpoints funcionan correctamente. Validaciones de entrada operativas. Manejo de errores con códigos HTTP correctos (404, 400, 409). CRUD completo de productos y categorías. Movimientos y alertas funcionales. |
| **JavaDoc profesional** | 15% | Cobertura 100% de clases públicas e interfaces. Cobertura 100% de métodos públicos con `@param`, `@return`, `@throws`. `@since "1.0"` en todas las clases. `mvn javadoc:javadoc` sin warnings. |
| **Performance Report** | 15% | `PERFORMANCE.md` con tabla completa de complejidades teóricas. Mediciones empíricas para 1k, 10k y 100k registros. Justificación de Big O para cada endpoint. Análisis de discrepancias teoría/realidad. |
| **Código limpio** | 10% | Nombres significativos. Sin código duplicado (DRY). Sin números mágicos (usar constantes o configuración). Formato consistente. Sin imports sin usar. Sin código comentado. |
| **Swagger/OpenAPI** | 5% | Documentación interactiva accesible en `http://localhost:8081/swagger-ui.html`. Endpoints documentados con descripciones y ejemplos. |

---

## 10. Formato de Entrega

1. **Repositorio Git** con historial de commits distribuido entre todos los integrantes del grupo (se evaluará la participación equitativa mediante git blame / git shortlog).
2. **Proyecto compilable:** `mvn clean package` debe ejecutarse sin errores ni warnings.
3. **Swagger accesible:** al ejecutar `mvn spring-boot:run`, la UI de Swagger debe estar disponible en `http://localhost:8081/swagger-ui.html` con todos los endpoints documentados.
4. **`PERFORMANCE.md`** en la raíz del proyecto con el reporte completo.
5. **Código fuente documentado:** `mvn javadoc:javadoc` sin warnings.
6. **Todos los tests pasando:** `mvn test` sin fallos.

### Estructura de entrega en Git

```
main
├── src/                  # Código fuente completo
├── pom.xml               # Maven POM
├── README.md             # Este documento
├── PERFORMANCE.md        # Reporte de performance
└── .gitignore            # Ignorar target/, .idea/, *.iml, etc.
```

---

## 11. Recursos y Referencias

- **Effective Java (3rd Edition)** — Joshua Bloch. *Item 29: Favor generic types*. Addison-Wesley, 2018.
- **Spring Boot Reference Documentation** — [Dependency Injection and IoC Container](https://docs.spring.io/spring-boot/reference/features/spring-application.html)
- **ConcurrentHashMap JavaDoc** — [java.util.concurrent.ConcurrentHashMap](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)
- **AtomicInteger JavaDoc** — [java.util.concurrent.atomic.AtomicInteger](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html)
- **Principios SOLID** — Robert C. Martin. *Clean Architecture*. Prentice Hall, 2017.
- **OpenAPI 3.0 Specification** — [springdoc-openapi v2.8.x](https://springdoc.org/)
- **Jakarta Bean Validation** — [Jakarta EE Specification](https://beanvalidation.org/2.0/spec/)
- **Java Records (JEP 395)** — [OpenJDK](https://openjdk.org/jeps/395)
- **Design Patterns: Elements of Reusable Object-Oriented Software** — Gamma, Helm, Johnson, Vlissides (GoF). *Strategy Pattern*, pp. 315-323.

---

> **Nota para los alumnos:** Este es el trabajo práctico **integrador** de la materia. Pone en práctica todos los conceptos vistos durante la cursada: diseño orientado a objetos, principios SOLID, patrones de diseño, genéricos, arquitectura en capas, desarrollo de APIs REST y documentación profesional. Dediquen tiempo a discutir el diseño en grupo **antes de escribir código**. Un buen diseño ahorra semanas de depuración.

---

*Facultad de Ingeniería — Programación III — 2026*
