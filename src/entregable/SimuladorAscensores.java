import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;


class Ascensor implements Runnable {
    private int id;
    private int pisoActual;
    private BlockingQueue<Solicitud> solicitudes;
    private List<Integer> lugaresDeDetencion;
    private int pisoObjetivo;
    private Semaphore semaforo;
    private int contador;

    public void agregarPisoDeDetencion(int piso) {
        if (!lugaresDeDetencion.contains(piso)) {
            lugaresDeDetencion.add(piso);
        }
    }

    public int getContador() {
        return contador;
    }

    public Ascensor(int id, BlockingQueue<Solicitud> solicitudes, List<Integer> lugaresDeDetencion, Semaphore semaforo) {
        this.id = id;
        this.pisoActual = 0;
        this.solicitudes = solicitudes;
        this.lugaresDeDetencion = lugaresDeDetencion;
        this.pisoObjetivo = 0;
        this.semaforo = semaforo;
        this.contador = 0;

    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                semaforo.acquire();
                Solicitud solicitud = solicitudes.take();
                semaforo.release();
                int pisoDestino = solicitud.getPisoDestino();
                 
        if (lugaresDeDetencion.contains(pisoActual)) {
            System.out.println("Ascensor " + id + " Cumplio con una solicitud de paso en en piso" + pisoActual);
            contador++;
        } 
                System.out.println("Ascensor " + id + " sale del piso " + pisoActual);
                moverAscensor(pisoDestino);
                contador++;
                System.out.println("Ascensor " + id + " ha llegado al piso " + pisoActual);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void moverAscensor(int pisoDestino) throws InterruptedException {
        int direccion = (pisoDestino - pisoActual) / Math.abs(pisoDestino - pisoActual);
        while (pisoActual != pisoDestino) {
            pisoActual += direccion;
            System.out.println("Ascensor " + id + " se encuentra en el piso " + pisoActual);
            Thread.sleep(1000);
            if (lugaresDeDetencion.contains(pisoActual)) {
                System.out.println("Ascensor " + id + " se detiene en el piso " + pisoActual);
                lugaresDeDetencion.remove(Integer.valueOf(pisoActual)); // Eliminar el piso de detención
                Thread.sleep(1000);
            }
        }
    }

    public void setPisoObjetivo(int pisoObjetivo) {
        this.pisoObjetivo = pisoObjetivo;
    }

    public int getPisoActual() {
        return pisoActual;
    }

    public int getPisoObjetivo() {
        return pisoObjetivo;
    }
    public int getId(){
        return this.id;
    }
}

class AsignadorSolicitudes implements Runnable {

    private Semaphore semaforo;
    private int ultimoAscensorAsignado = 0;

    private List<BlockingQueue<Solicitud>> colasAscensores;
    private Queue<Solicitud> solicitudesPendientes;
    private Map<Integer, Ascensor> mapaAscensores;

    public AsignadorSolicitudes(Queue<Solicitud> solicitudesPendientes, List<BlockingQueue<Solicitud>> colasAscensores, Map<Integer, Ascensor> mapaAscensores, Semaphore semaforo) {
        this.solicitudesPendientes = solicitudesPendientes;
        this.colasAscensores = colasAscensores;
        this.mapaAscensores = mapaAscensores;
        this.semaforo = semaforo;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                while (!solicitudesPendientes.isEmpty()) {
                    Solicitud solicitud = solicitudesPendientes.poll();
                    int ascensorId = asignarAscensor(solicitud.getPisoDestino());
                    colasAscensores.get(ascensorId).put(solicitud);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int asignarAscensor(int pisoDestino) {
        // Se utiliza la variable últimoAscensorAsignado para determinar el siguiente ascensor a asignar
        int ascensorId = ultimoAscensorAsignado % colasAscensores.size();
        
        // Se obtiene el ascensor correspondiente del mapa
        Ascensor ascensor = mapaAscensores.get(ascensorId);
    
        // Se asigna el piso objetivo al ascensor
        ascensor.setPisoObjetivo(pisoDestino);

        ascensor.agregarPisoDeDetencion(pisoDestino);
    
        // Se incrementa el contador para la próxima asignación
        ultimoAscensorAsignado++;
    
        // Se retorna el ID del ascensor asignado
        return ascensorId;
    }
    
    }

    class Solicitud {
        private int pisoDestino;

        public Solicitud(int pisoDestino) {
            this.pisoDestino = pisoDestino;
        }

        public int getPisoDestino() {
            return pisoDestino;
        }

    }

    public class SimuladorAscensores {
        public static void main(String[] args) {
            int numAscensores = 4;
            List<BlockingQueue<Solicitud>> colasAscensores = new ArrayList<>();
            Queue<Solicitud> solicitudesPendientes = new LinkedList<>(); // Cambiado de PriorityQueue a LinkedList
            Thread[] ascensores = new Thread[numAscensores];
            Semaphore semaforo = new Semaphore(1); // solo un ascensor a la vez puede tomar una solicitud
            Map<Integer, Ascensor> mapaAscensores = new HashMap<>();

            for (int i = 0; i < numAscensores; i++) {
                colasAscensores.add(new ArrayBlockingQueue<>(21)); // al poner capacidad 11, llega hasta el piso 10, dado que cuenta a 0 para el indice
                List<Integer> lugaresDeDetencion = new ArrayList<>();
                Ascensor ascensor = new Ascensor(i + 1, colasAscensores.get(i), lugaresDeDetencion, semaforo);
                mapaAscensores.put(i, ascensor);
                ascensores[i] = new Thread(ascensor);
                ascensores[i].start();
            }


        AsignadorSolicitudes asignadorSolicitudes = new AsignadorSolicitudes(solicitudesPendientes, colasAscensores, mapaAscensores, semaforo);
        Thread hiloAsignador = new Thread(asignadorSolicitudes);
        hiloAsignador.start();

        String archivo = "/Users/tincho/Desktop/sistemasOperativos-ascensor-main/src/entregable/instrucciones.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                int pisoDestino = Integer.parseInt(linea);
                Solicitud solicitud = new Solicitud(pisoDestino);
                solicitudesPendientes.offer(solicitud);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(30000); // ajusta este valor segun sea necesario, cuantas mas solicitudes haya, mas tiempo debe esperar
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Ahora interrumpe los hilos de los ascensores
        for (Thread ascensor : ascensores) {
            ascensor.interrupt();
        }
        
        // Espera a que todos los hilos terminen antes de imprimir las estadísticas
        for (Thread ascensor : ascensores) {
            try {
                ascensor.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }        

        System.out.println(); // Esto imprime un salto de línea
        System.out.println("Estadisticas de Ascensores: ");

        // Imprimir el contador de cada ascensor
        for (Map.Entry<Integer, Ascensor> entry : mapaAscensores.entrySet()) {
            Ascensor ascensor = entry.getValue();
            System.out.println("El ascensor " + ascensor.getId() + " ha procesado " + ascensor.getContador() + " solicitudes.");
        }
    }
}
