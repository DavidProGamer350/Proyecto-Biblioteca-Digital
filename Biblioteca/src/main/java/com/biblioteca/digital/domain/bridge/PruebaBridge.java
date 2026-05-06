package com.biblioteca.digital.domain.bridge;
import com.biblioteca.digital.domain.bridge.acceso.*;
import com.biblioteca.digital.domain.bridge.libro.*;

public class PruebaBridge {

    public static void main(String[] args) {

        System.out.println(" PRUEBA BRIDGE - BIBLIOTECA DIGITAL\n");

        // 1. Crear accesos
        Acceso accesoGratis = new AccesoGratis();
        Acceso accesoPremium = new AccesoPremium();

        // 2. Crear libros 
        LibroBridge libro1 = new LibroPdfBridge(accesoGratis);
        LibroBridge libro2 = new LibroPdfBridge(accesoPremium);
        LibroBridge libro3 = new LibroEpubBridge(accesoPremium);

        // 3. Mostrar combinaciones
        System.out.println("1. COMBINACIONES:\n");

        System.out.println("Libro1 -> " + libro1.getFormato() + " | " + libro1.tipoAcceso());
        System.out.println("Libro2 -> " + libro2.getFormato() + " | " + libro2.tipoAcceso());
        System.out.println("Libro3 -> " + libro3.getFormato() + " | " + libro3.tipoAcceso());

        System.out.println("\n Mismo formato, distinto acceso \n");

        // 4. Validar comportamiento
        System.out.println("2. VALIDANDO PERMISOS:\n");

        System.out.println("Libro1 puede descargar: " + libro1.puedeDescargar());
        System.out.println("Libro2 puede descargar: " + libro2.puedeDescargar());

        System.out.println("\n El acceso cambia el comportamiento \n");

        // 5. Independencia
        System.out.println("3. INDEPENDENCIA:\n");

        System.out.println("Formato libro1: " + libro1.getFormato());
        System.out.println("Formato libro2: " + libro2.getFormato());

        System.out.println("Acceso libro1: " + libro1.tipoAcceso());
        System.out.println("Acceso libro2: " + libro2.tipoAcceso());

        System.out.println("\n Las dimensiones son independientes \n");

    }
}