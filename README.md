# sistemasOperativos-ascensor

Este programa simula el comportamiento de un sistema de ascensores. A continuación, se explica cada parte del código:

Ascensor (Class): Esta clase representa a un ascensor, tiene propiedades como su identificador (id), el piso actual en el que se encuentra, las solicitudes de viaje que tiene asignadas, los lugares de detención y el semáforo utilizado para la sincronización. Además, implementa la interfaz Runnable, lo que permite que cada instancia de un Ascensor se pueda ejecutar en un hilo separado.

AsignadorSolicitudes (Class): Esta clase es responsable de asignar solicitudes de viaje a los ascensores. También implementa Runnable, por lo que puede operar en su propio hilo, asignando solicitudes a medida que se encuentran disponibles.

Solicitud (Class): Representa una solicitud de viaje. Cada solicitud tiene un piso de destino y un tiempo en que se hace.

SimuladorAscensores (Main Class): Esta es la clase principal del programa. Inicializa los ascensores, las colas de solicitudes, inicia todos los hilos y lee las solicitudes de un archivo.

El programa funciona de la siguiente manera:

- Cada ascensor opera en su propio hilo, y puede moverse entre los pisos, atendiendo a las solicitudes que se le han asignado.
  
- El AsignadorSolicitudes opera en un hilo separado, y se encarga de asignar las solicitudes a los ascensores según las solicitudes que tenga en su cola y las reglas definidas en su método asignarAscensor.
  
- Las solicitudes se leen de un archivo y se agregan a una cola de solicitudes pendientes en orden de tiempo.

- El semáforo se utiliza para garantizar que solo un ascensor a la vez puede tomar una solicitud de la cola.
  
Por lo tanto, el programa simula el funcionamiento de un sistema de ascensores, leyendo solicitudes de un archivo, asignándolas a los ascensores y permitiendo que los ascensores se muevan para cumplir con estas solicitudes.
