\# README.md


\## Descripción


Aplicación de chat cliente-servidor en Java que permite:


\* Mensajes públicos

\* Lista de usuarios conectados en tiempo real

\* Mensajería privada



\## Compilación



Ubíquese en la carpeta del proyecto y ejecute:


javac jchat/\*.java


\## Ejecución



\### 1. Iniciar servidor


java jchat.Servidor



\### 2. Iniciar clientes (en diferentes terminales)



java jchat.Cliente


\## Uso


\### Mensajes públicos

Muestra mensajes globales y privados.



\### Lista de usuarios

Muestra una lista con todos los usuarios conectados y actualiza a los que se desconectan


\[Usuarios conectados]: Juan,Ana,Pedro


\### Mensajes privados


Formato:


@Usuario:mensaje


Ejemplo:


@Ana:Hola


Resultado:


\* Ana recibe:


(Privado) Juan: Hola


\* Juan recibe:


(Privado a Ana): Hola


\## Notas


\* Los nombres de usuario no deben repetirse

\* Se eliminan espacios con trim()

\* Sensible a mayúsculas/minúsculas


\# INFORME


\## 1. Estructura de datos


Se utiliza una estructura tipo Map:


Map<String, Flujo> usuarios


Donde:



\* \*\*Clave (String):\*\* nombre del usuario

\* \*\*Valor (Flujo):\*\* objeto que contiene su conexión (socket + streams)



Esto permite:



\* Acceso rápido O(1)

\* Identificar usuarios únicos

\* Enviar mensajes directos fácilmente




\## 2. Gestión de usuarios



\### Conexión



\* Se valida nombre

\* Se agrega al Map

\* Se notifica a todos



\### Desconexión



\* Se elimina del Map

\* Se notifica a todos


\## 3. Protocolo de lista de usuarios



Se define un mensaje interno con prefijo:



\_\_USERLIST\_\_:u1,u2,u3

[Usuarios conectados]: usuario1, usuario2


\### Funcionamiento:



1\. Se construye la lista desde el Map

2\. Se envía a todos los clientes

3\. El cliente detecta el prefijo

4\. Se procesa por separado del chat



\## 4. Mensajería privada



\### Formato:


@destino:mensaje


\### Proceso:



1\. Detectar si inicia con '@'

2\. Separar destino y mensaje

3\. Buscar usuario en el Map

4\. Enviar solo al destinatario



\### Validaciones:



\* Usuario existe

\* Eliminación de espacios (trim)


\##  5. Conclusión



El sistema implementa correctamente:



\* Comunicación cliente-servidor

\* Difusión de mensajes

\* Gestión dinámica de usuarios

\* Mensajería privada eficiente



El uso de Map permite escalabilidad y eficiencia en la búsqueda de usuarios.



Imágenes de prueba en netbeans: 



!\[](imagenes/InicioListaConectados.png)



!\[](imagenes/mensajesGeneralesYPrivadosLista.png)



!\[](imagenes/mensajesPrivadosListaActualizadaDesconectados.png)



