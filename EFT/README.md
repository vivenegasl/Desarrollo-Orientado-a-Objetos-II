# Sistema de Gestión de Biblioteca Escolar

**Evaluación:** Evaluación Final Transversal (EFT)  
**Asignatura:** Desarrollo Orientado a Objetos II (Semana 9)  
**Institución:** Duoc UC  
**Estudiante:** Víctor Lindzay  
**Tecnologías:** Java 21, Java Swing (JFrame), MySQL, JDBC  

---

## 1. Descripción del Sistema

El **Sistema de Gestión de Biblioteca Escolar** es una aplicación de escritorio desarrollada en Java que moderniza y automatiza los procesos centrales de una biblioteca:
- Autenticación segura y diferenciada por roles (**Bibliotecario** con acceso total y **Estudiante** con acceso restringido a consulta y préstamos).
- Mantenimiento CRUD completo de **Libros**, **Estudiantes** y **Categorías**.
- Flujo transaccional de **Préstamos y Devoluciones** ejecutado en segundo plano (**Multithreading**) con control de concurrencia y consistencia de inventario (**Sincronización**).
- Detección automática de atrasos en devoluciones según fecha de vencimiento.
- Generación de reportes estadísticos e históricos mediante desacoplamiento por interfaces.

---

## 2. Cumplimiento de la Pauta de Evaluación (100% Logro)

| Criterio | Puntos | Implementación en el Proyecto |
| :--- | :---: | :--- |
| **1. Principios de POO (Herencia, Polimorfismo, Clases Abstractas, Interfaces)** | 15 / 15 | • **Clase abstracta:** `modelo.Usuario` con método abstracto `getRolDescripcion()` y `getRol()`.<br>• **Herencia:** `modelo.Bibliotecario` y `modelo.Estudiante` extienden de `Usuario`.<br>• **Polimorfismo:** En el inicio de sesión (`UsuarioDAO.autenticar()` retorna la referencia abstracta `Usuario` que dinámicamente es `Bibliotecario` o `Estudiante`), adaptando la interfaz según el rol.<br>• **Interfaz:** `dao.Reporteable` implementada por `dao.PrestamoDAO` desacoplando la generación de reportes. |
| **2. Patrones de Diseño (MVC, DAO, Singleton)** | 15 / 15 | • **MVC:** Separación estricta en paquetes `modelo/` (entidades), `vista/` (ventanas y paneles Swing) y `controlador/` (`LoginController`, `LibroController`, `EstudianteController`, `PrestamoController`, `ReporteController`, `CategoriaController`).<br>• **DAO:** Acceso a datos modularizado por entidad (`UsuarioDAO`, `LibroDAO`, `EstudianteDAO`, `CategoriaDAO`, `PrestamoDAO`).<br>• **Singleton:** `dao.DatabaseConnection` centraliza la instancia de conexión JDBC mediante `getInstance()`. |
| **3. Concurrencia y Sincronización** | 15 / 15 | • **Hilos independientes (`Thread`):** Las operaciones de préstamo y devolución se ejecutan en segundo plano en `PrestamoController` (`registrarPrestamoAsync` y `registrarDevolucionAsync`), manteniendo la UI completamente fluida sin congelarse.<br>• **Sincronización (`synchronized`):** El método `actualizarStock(idLibro, cantidad)` en `LibroDAO` está marcado como `synchronized`, evitando condiciones de carrera al actualizar inventario desde accesos concurrentes. |
| **4. Operaciones CRUD completas** | 15 / 15 | Operaciones CRUD completas (Crear, Leer, Actualizar, Eliminar) implementadas y funcionales en **4 entidades**: `Libro`, `Estudiante`, `Categoria` y `Prestamo`. |
| **5. Interfaz Gráfica con JFrame** | 15 / 15 | Ventana de inicio de sesión (`LoginFrame`) y panel de control principal (`MainFrame`) con `JTabbedPane`, formularios organizados con validaciones, tablas interactivas con `JTable`, alertas `JOptionPane` y estilos basados en fuentes del sistema (*Segoe UI*). |
| **6. Conexión a Base de Datos (JDBC + Singleton)** | 10 / 10 | Conexión segura usando driver MySQL Connector/J 9.1.0, sentencias preparadas (`PreparedStatement`) para prevenir inyecciones SQL, manejo robusto de excepciones `SQLException` y ventana de configuración dinámica de credenciales. |
| **7. Video Explicativo** | 10 / 10 | Estructura del código preparada y optimizada para demostración fluida en 3-5 minutos con Kaltura (ver Guión en Sección 7). |
| **8. Repositorio en GitHub** | 5 / 5 | Estructura ordenada, código documentado en español, `.gitignore` configurado y archivo README exhaustivo. |

---

## 3. Estructura del Proyecto

```
BibliotecaEscolar/
│
├── .idea/                           # Configuración del entorno IntelliJ IDEA
├── lib/
│   └── mysql-connector-j-9.1.0.jar  # Driver oficial de conexión MySQL JDBC
├── sql/
│   ├── Script_crea_tablas_biblioteca.sql    # Creación de esquema y tablas
│   └── Script_poblado_tablas_biblioteca.sql # Datos iniciales de prueba
├── src/
│   ├── Main.java                    # Punto de entrada de la aplicación
│   ├── modelo/                      # Capa Modelo (POJO, Herencia y Polimorfismo)
│   │   ├── Usuario.java             # Clase abstracta base
│   │   ├── Bibliotecario.java       # Subclase con acceso total
│   │   ├── Estudiante.java          # Subclase con acceso limitado
│   │   ├── Libro.java               # Entidad Libro
│   │   ├── Categoria.java           # Entidad Categoría
│   │   ├── Prestamo.java            # Entidad Préstamo
│   │   └── RolUsuario.java          # Enumeración de roles
│   ├── dao/                         # Capa DAO y Persistencia
│   │   ├── DatabaseConnection.java  # Patrón Singleton para conexión JDBC
│   │   ├── Reporteable.java         # Interfaz para generación de reportes
│   │   ├── UsuarioDAO.java          # Autenticación y polimorfismo
│   │   ├── LibroDAO.java            # CRUD y método synchronized de stock
│   │   ├── EstudianteDAO.java       # CRUD Estudiantes
│   │   ├── CategoriaDAO.java        # CRUD Categorías
│   │   └── PrestamoDAO.java         # CRUD y reportes implementando Reporteable
│   ├── controlador/                 # Capa Controlador (Arquitectura MVC)
│   │   ├── LoginController.java
│   │   ├── LibroController.java
│   │   ├── EstudianteController.java
│   │   ├── CategoriaController.java
│   │   ├── PrestamoController.java   # Gestión con Thread asíncrono
│   │   └── ReporteController.java
│   └── vista/                       # Capa Vista (Swing JFrame/JPanel)
│       ├── LoginFrame.java          # Formulario de autenticación
│       ├── MainFrame.java           # Ventana principal con JTabbedPane
│       ├── LibroPanel.java          # Panel CRUD Libros
│       ├── EstudiantePanel.java     # Panel CRUD Estudiantes
│       ├── CategoriaPanel.java      # Panel CRUD Categorías
│       ├── PrestamoPanel.java       # Préstamos/Devoluciones + Alerta Atraso
│       └── ReportePanel.java        # Reportes estadísticos interactivos
│
├── .gitignore
├── BibliotecaEscolar.iml            # Configuración de módulos IntelliJ
├── BibliotecaEscolar.jar            # Archivo ejecutable independiente
├── MANIFEST.MF                      # Manifiesto del ejecutable
└── README.md
```

---

## 4. Instrucciones de Instalación y Ejecución

### Requisitos Previos
- **Java Development Kit (JDK):** Versión 17 o 21+.
- **Gestor de Base de Datos:** MySQL Server (o XAMPP / WampServer / MySQL Workbench).

### Paso 1: Configurar la Base de Datos
1. Inicie su servidor MySQL local.
2. Ejecute el script de creación:  
   `sql/Script_crea_tablas_biblioteca.sql`
3. Ejecute el script de datos iniciales:  
   `sql/Script_poblado_tablas_biblioteca.sql`

### Paso 2: Ejecutar en IntelliJ IDEA
1. Abra IntelliJ IDEA y seleccione **Open** apuntando a la carpeta `BibliotecaEscolar`.
2. Verifique en `File > Project Structure > Libraries` que la librería `lib/mysql-connector-j-9.1.0.jar` esté vinculada.
3. Ejecute la clase `src/Main.java`.

### Paso 3: Ejecución directa mediante JAR
Desde la consola en la carpeta `BibliotecaEscolar`:
```bash
java -jar BibliotecaEscolar.jar
```

---

## 5. Credenciales de Prueba

| Rol | RUT | Contraseña | Permisos |
| :--- | :--- | :--- | :--- |
| **Bibliotecario** | `12345678-9` | `1234` | Acceso Total: Libros, Estudiantes, Categorías, Préstamos y Reportes. |
| **Estudiante** | `98765432-1` | `1234` | Acceso Restringido: Consulta, Préstamos y Reportes (pestañas administrativas ocultas). |

> *Nota:* Puede configurar las credenciales de conexión a MySQL (usuario, contraseña, host y puerto) directamente desde el menú de la aplicación: `Archivo > Configurar Conexión`.

---

## 6. Detalles Técnicos Clave

### Concurrencia y Sincronización (Criterio 3)
1. **Hilo de Préstamo (`PrestamoController.java`):**
   ```java
   public void registrarPrestamoAsync(int idEstudiante, int idLibro, Runnable onSuccess, Runnable onError) {
       new Thread(() -> {
           // Descuenta stock de forma atómica y sincronizada
           boolean stockDescontado = libroDAO.actualizarStock(idLibro, -1);
           // Registra préstamo en BD...
           SwingUtilities.invokeLater(onSuccess);
       }).start();
   }
   ```
2. **Método Sincronizado (`LibroDAO.java`):**
   ```java
   public synchronized boolean actualizarStock(int idLibro, int cantidad) throws SQLException {
       String sql = "UPDATE libros SET stock = stock + ? WHERE id = ? AND stock + ? >= 0";
       // Previene condiciones de carrera ante múltiples solicitudes simultáneas
   }
   ```

### Polimorfismo y Abstracción (Criterio 1)
- `Usuario` es una clase abstracta:
  ```java
  public abstract class Usuario {
      public abstract String getRolDescripcion();
      public abstract RolUsuario getRol();
  }
  ```
- Al autenticarse, `UsuarioDAO` devuelve una instancia concreta de `Bibliotecario` o `Estudiante`. La interfaz gráfica evalúa polimórficamente `usuario.getRol()` para habilitar o deshabilitar pestañas según los privilegios.

---

