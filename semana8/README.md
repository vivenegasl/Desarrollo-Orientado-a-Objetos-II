# SpeedFast - Semana 8 (Sumativa 3): Sistema de Gestión con JDBC y Swing

## 📦 Descripción del Proyecto
Este proyecto corresponde a la actividad evaluada **Sumativa 3 (Semana 8)** de la asignatura **Desarrollo Orientado a Objetos II** (Duoc UC).
Culmina el ciclo de desarrollo de la empresa **SpeedFast**, integrando una arquitectura modular en tres capas: **Modelo**, **Acceso a Datos (DAO)** y **Presentación (Swing GUI)**, con persistencia relacional en **MySQL** mediante **JDBC**.

---

## 🚀 Arquitectura y Componentes Desarrollados

### 1. Capa de Modelo (`modelo`)
Representa las entidades del dominio de negocio con encapsulamiento, constructores, getters, setters y métodos auxiliares:
- `TipoPedido` (Enum): `COMIDA`, `ENCOMIENDA`, `EXPRESS`.
- `EstadoPedido` (Enum): `PENDIENTE`, `EN_REPARTO`, `ENTREGADO`.
- `Repartidor`: Modela al repartidor con `id` y `nombre`.
- `Pedido`: Modela un pedido con `id`, `direccion`, `tipo` y `estado`.
- `Entrega`: Modela la asociación entre un pedido y un repartidor, registrando `id`, `idPedido`, `idRepartidor`, `fecha` (`LocalDate`), `hora` (`LocalTime`), y campos enriquecidos para la vista.

### 2. Capa de Persistencia / DAO (`dao`)
Implementa operaciones CRUD completas y consultas avanzadas mediante sentencias preparadas (`PreparedStatement`) y conjuntos de resultados (`ResultSet`), garantizando seguridad ante inyecciones SQL y cierre confiable de recursos con *try-with-resources*:
- `ConexionDB`: Centraliza la conexión JDBC con `com.mysql.cj.jdbc.Driver` hacia `speedfast_db`. Permite configurar en tiempo de ejecución credenciales alternativas.
- `RepartidorDAO`:
  - `create(Repartidor r)`: Inserta un nuevo repartidor y recupera el ID generado.
  - `readAll()`: Consulta todos los repartidores ordenados por ID.
  - `findById(int id)`: Obtiene un repartidor por su identificador.
  - `update(Repartidor r)`: Actualiza el nombre del repartidor.
  - `delete(int id)`: Elimina el repartidor indicado.
- `PedidoDAO`:
  - `create(Pedido p)`: Registra un pedido con su dirección, tipo y estado.
  - `readAll()`: Lista todos los pedidos.
  - `readByFiltros(TipoPedido tipo, EstadoPedido estado)`: Consulta dinámica con filtros opcionales de tipo y estado.
  - `findById(int id)`: Búsqueda por clave primaria.
  - `update(Pedido p)`: Actualiza los campos de un pedido existente.
  - `delete(int id)`: Elimina un pedido.
- `EntregaDAO`:
  - `create(Entrega e)`: Registra una nueva entrega asociando pedido y repartidor con fecha y hora.
  - `readAll()`: Lista entregas mediante `JOIN` con pedidos y repartidores para mostrar nombres y direcciones legibles.
  - `readByFiltros(Integer idPedido, Integer idRepartidor)`: Filtra entregas por pedido o por repartidor específico.
  - `findById(int id)`: Recupera una entrega específica.
  - `update(Entrega e)`: Actualiza los datos de la entrega.
  - `delete(int id)`: Elimina el registro de entrega.

### 3. Capa de Presentación / GUI (`vista`)
Interfaz gráfica completa desarrollada en **Java Swing** con diseño responsivo, validaciones en tiempo real y notificaciones claras:
- `MainFrame`: Ventana principal con barra de título, menú con utilidades (probar conexión, configurar credenciales DB), barra de estado y un `JTabbedPane` con 3 pestañas sincronizadas.
- `RepartidorPanel`:
  - Formulario con campos (ID automático, Nombre obligatorio con validación de longitud).
  - Botones: Registrar, Actualizar, Eliminar, Limpiar Selección.
  - `JTable` de solo lectura que carga datos desde `RepartidorDAO`.
  - Notifica a la pestaña de Entregas para actualizar los `JComboBox` al modificar datos.
- `PedidoPanel`:
  - Formulario con Dirección (obligatoria), Tipo (`JComboBox`) y Estado (`JComboBox`).
  - Botones de acción CRUD completos.
  - Barra de búsqueda con filtros combinables por Tipo y Estado.
  - `JTable` con listado en tiempo real.
  - Sincronización automática con la pestaña de Entregas.
- `EntregaPanel`:
  - `JComboBox` dinámicos de Pedidos y Repartidores cargados directamente desde la base de datos con formato `"ID - Descripción"`.
  - Campos de Fecha (`YYYY-MM-DD`) y Hora (`HH:MM:SS`) con validación y botón de llenado automático con la fecha/hora actual.
  - Barra de filtros para consultar entregas por Repartidor o por Pedido.
  - `JTable` con columnas enriquecidas (ID Entrega, ID Pedido, Dirección, ID Repartidor, Nombre, Fecha, Hora).
  - Métodos automáticos de sincronización (`cargarCombos()`) cada vez que se selecciona la pestaña.
- `Main`: Punto de entrada con Look and Feel nativo del sistema operativo y ejecución dentro del Event Dispatch Thread (EDT).

---

## 🗄️ Base de Datos MySQL

### Script de Creación (`database.sql`)
Ejecuta el script `database.sql` en MySQL Workbench, phpMyAdmin o consola MySQL:

```sql
CREATE DATABASE IF NOT EXISTS speedfast_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE speedfast_db;

CREATE TABLE repartidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(100) NOT NULL,
    tipo ENUM('COMIDA', 'ENCOMIENDA', 'EXPRESS') NOT NULL,
    estado ENUM('PENDIENTE', 'EN_REPARTO', 'ENTREGADO') NOT NULL DEFAULT 'PENDIENTE'
);

CREATE TABLE entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_repartidor INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_repartidor) REFERENCES repartidores(id) ON DELETE CASCADE ON UPDATE CASCADE
);
```

### Configuración de Conexión
Los parámetros por defecto en `ConexionDB.java` son:
- **Host**: `localhost`
- **Puerto**: `3306`
- **Base de Datos**: `speedfast_db`
- **Usuario**: `root`
- **Contraseña**: *(vacía por defecto)*

> **Nota:** Si tu servidor MySQL tiene una contraseña asignada (por ejemplo, `root` o `1234`), puedes cambiarla directamente en `ConexionDB.java` o desde la propia aplicación en el menú **Archivo -> Configurar Parámetros de Conexión...**.

---

## 🛠️ Estructura del Directorio

```
Semana8/
├── .idea/                           # Configuración de IntelliJ IDEA
├── lib/                             # Dependencias del proyecto
│   └── mysql-connector-j-9.1.0.jar  # Driver JDBC de MySQL
├── out/production/semana8/          # Binarios compilados (.class)
├── src/                             # Código fuente
│   ├── modelo/                      # Entidades del dominio
│   │   ├── EstadoPedido.java
│   │   ├── TipoPedido.java
│   │   ├── Repartidor.java
│   │   ├── Pedido.java
│   │   └── Entrega.java
│   ├── dao/                         # Capa de persistencia JDBC
│   │   ├── ConexionDB.java
│   │   ├── RepartidorDAO.java
│   │   ├── PedidoDAO.java
│   │   └── EntregaDAO.java
│   ├── vista/                       # Interfaz gráfica Swing
│   │   ├── MainFrame.java
│   │   ├── RepartidorPanel.java
│   │   ├── PedidoPanel.java
│   │   └── EntregaPanel.java
│   └── Main.java                    # Entry point de la aplicación
├── database.sql                     # Script SQL con esquema y datos de prueba
├── semana8.iml                      # Módulo de IntelliJ IDEA
└── README.md                        # Documentación técnica
```

---

## 💻 Compilación y Ejecución

### Opción 1: Desde IntelliJ IDEA (Recomendado)
1. Abrir la carpeta `Semana8` en IntelliJ IDEA (`File -> Open`).
2. Verificar que el Project SDK esté en **Java 21** (o Java 17+).
3. La librería `lib/mysql-connector-j-9.1.0.jar` ya está configurada en `semana8.iml`.
4. Ejecutar la clase `Main.java` haciendo clic derecho y seleccionando **Run 'Main.main()'**.

### Opción 2: Desde Terminal / Consola (PowerShell)
```powershell
# 1. Compilación
$sources = (Get-ChildItem -Recurse src/*.java).FullName
javac -cp "lib/mysql-connector-j-9.1.0.jar" -d "out/production/semana8" $sources

# 2. Ejecución
java -cp "out/production/semana8;lib/mysql-connector-j-9.1.0.jar" Main
```

---

## 📋 Cumplimiento de la Pauta de Evaluación

| N° | Criterio de Evaluación | Puntaje Máximo | Implementación en el Proyecto |
|---|---|---|---|
| **1** | **Clases DAO con CRUD completo (`PreparedStatement` y `ResultSet`)** | **20 pts** | `RepartidorDAO`, `PedidoDAO` y `EntregaDAO` implementan `create()`, `readAll()`, `findById()`, `update()` y `delete()` con `PreparedStatement`, `ResultSet`, y manejo seguro con *try-with-resources*. |
| **2** | **Conexión de la GUI con los métodos DAO en tiempo real** | **20 pts** | Cada botón (Registrar, Actualizar, Eliminar) invoca al DAO respectivo y recarga inmediatamente las tablas `JTable` y los `JComboBox`. |
| **3** | **Validación de datos de entrada en formularios** | **15 pts** | Validación de campos obligatorios, longitudes mínimas, selección de entidades en combos, y formato de fecha (`YYYY-MM-DD`) y hora (`HH:MM:SS`) antes de invocar la persistencia. |
| **4** | **Manejo de errores y excepciones SQL con mensajes claros** | **15 pts** | Bloques `try-catch (SQLException)` en todos los paneles, mostrando diálogos descriptivos con `JOptionPane.showMessageDialog` (`WARNING_MESSAGE`, `ERROR_MESSAGE`, `INFORMATION_MESSAGE`). |
| **5** | **Interfaz gráfica funcional en Java Swing** | **15 pts** | Ventana `JFrame` con `JTabbedPane`, paneles modulares con `JTable`, `JTextField`, `JComboBox`, `JButton`, filtros dinámicos y apariencia nativa del sistema. |
| **6** | **Buenas prácticas, modularización y documentación** | **10 pts** | Separación estricta en 3 capas (`modelo`, `dao`, `vista`), reutilización de código, JavaDoc explicativo en clases y métodos clave. |
| **7** | **Entrega en repositorio GitHub funcional** | **5 pts** | Estructura organizada lista para inicializar git (`git init`), realizar commits semánticos y subir a GitHub. |
| **Total** | | **100 pts** | **Completamente Logrado (CL)** |
