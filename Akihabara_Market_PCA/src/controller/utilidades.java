package controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class utilidades {

    // Pide un número entero al usuario.
    // mensaje: mensaje que se mostrará al usuario
    // retorna el número entero ingresado por el usuario
    public static int pedirEntero(String mensaje) {
        Scanner scan = new Scanner(System.in);
        int valor = 0;
        boolean error;
        do {
            error = false;
            System.out.print(mensaje);
            try {
                valor = Integer.parseInt(scan.next());
            } catch (Exception e) {
                System.out.println("[ERROR] Valor incorrecto");
                error = true;
            }
        } while (error);
        
        return valor;
    }

    // Pide una cadena de texto al usuario.
    // mensaje: mensaje que se mostrará al usuario
    // retorna la cadena ingresada por el usuario
    public static String pedirString(String mensaje) {
        Scanner scan = new Scanner(System.in);
        String valor = null;
        boolean error;
        do {
            error = false;
            System.out.print(mensaje);
            try {
                valor = scan.nextLine();
            } catch (Exception e) {
                System.out.println("[ERROR] Valor incorrecto");
                error = true;
            }
        } while (error);
        
        return valor;
    }

    // Pide un valor booleano al usuario.
    // mensaje: mensaje que se mostrará al usuario
    // retorna el valor booleano ingresado por el usuario
    public static boolean pedirBooleano(String mensaje) {
        Scanner scan = new Scanner(System.in);
        boolean valor = false;
        boolean error;
        do {
            error = false;
            System.out.print(mensaje + " (true/false): ");
            try {
                valor = Boolean.parseBoolean(scan.next());
            } catch (Exception e) {
                System.out.println("[ERROR] Valor incorrecto");
                error = true;
            }
        } while (error);
        
        return valor;
    }

    // Recorre un ArrayList y muestra sus elementos.
    // lista: el ArrayList a recorrer
    public static void recorrerArrayList(ArrayList<?> lista) {
        for (Object elemento : lista) {
            System.out.println(elemento);
        }
    }

    // Recorre un HashMap y muestra sus claves y valores.
    // mapa: el HashMap a recorrer
    public static void recorrerHashMap(HashMap<?, ?> mapa) {
        for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
            System.out.println("Clave: " + entrada.getKey() + ", Valor: " + entrada.getValue());
        }
    }

    // Recorre un TreeMap y muestra sus claves y valores.
    // mapa: el TreeMap a recorrer
    public static void recorrerTreeMap(TreeMap<?, ?> mapa) {
        for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
            System.out.println("Clave: " + entrada.getKey() + ", Valor: " + entrada.getValue());
        }
    }

    // Recorre un LinkedHashMap y muestra sus claves y valores.
    // mapa: el LinkedHashMap a recorrer
    public static void recorrerLinkedHashMap(LinkedHashMap<?, ?> mapa) {
        for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
            System.out.println("Clave: " + entrada.getKey() + ", Valor: " + entrada.getValue());
        }
    }
}