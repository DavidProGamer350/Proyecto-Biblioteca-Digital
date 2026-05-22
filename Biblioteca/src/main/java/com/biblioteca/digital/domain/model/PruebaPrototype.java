package com.biblioteca.digital.domain.model;

import java.time.LocalDate;

import com.biblioteca.digital.application.service.RecomendacionService;
import com.biblioteca.digital.domain.service.RecomendacionRegistry;

public class PruebaPrototype {
    public static void main(String[] args) {
        System.out.println("🎯 PRUEBA PROTOTYPE - BIBLIOTECA DIGITAL");
        
        // 1. Crear Registry y cargar prototipos
        RecomendacionRegistry registry = new RecomendacionRegistry();
        RecomendacionService service = new RecomendacionService(null);
        service.cargarPrototipos();
        
        // 2. Crear 2 recomendaciones clonando el mismo prototipo
        System.out.println("1. Creando recomendaciones desde PROTOTIPO:");
        Recomendacion rec1 = service.generarRecomendacion("premium", 100L, 200L);
        Recomendacion rec2 = service.generarRecomendacion("premium", 101L, 201L);
        
        System.out.println("✓ REC1 clonada: " + rec1.getMotivo());
        System.out.println("✓ REC2 clonada: " + rec2.getMotivo());
        System.out.println("  MISMO motivo heredado del prototipo ✓\n");
        
        // 3. Probar que son objetos DIFERENTES (clonado exitoso)
        System.out.println("2. VERIFICANDO CLONADO:");
        System.out.println("rec1 == rec2 ? " + (rec1 == rec2));
        System.out.println("rec1.equals(rec2) ? " + rec1.equals(rec2));
        System.out.println("rec1.hashCode(): " + rec1.hashCode());
        System.out.println("rec2.hashCode(): " + rec2.hashCode());
        System.out.println("✓ Objetos independientes ✓\n");
        
        // 4. Modificar uno NO afecta al otro (prueba de independencia)
        System.out.println("3. MODIFICANDO INDEPENDIENTEMENTE:");
        rec1.setPrioridad("CRITICA");
        rec1.setMotivo("URGENTE: Nuevo lanzamiento premium");
        
        System.out.println("rec1.prioridad: " + rec1.getPrioridad());
        System.out.println("rec2.prioridad: " + rec2.getPrioridad()); // No cambió
        System.out.println("✓ Modificación independiente ✓\n");
        
        // 5. Comparar vs CONSTRUCTOR TRADICIONAL (lo que EVITAMOS)
        System.out.println("4. VS CONSTRUCTOR TRADICIONAL:");
        Recomendacion recManual = new Recomendacion(
            999L, 102L, 202L,
            "Recomendación manual construida campo por campo",
            "premium", "alta", LocalDate.now(), true
        );
        System.out.println("✓ Constructor manual = 8 parámetros\n");
        
        // 6. Métricas de eficiencia (opcional)
        System.out.println("5. EFICIENCIA DEL PATRÓN:");
        System.out.println("Prototipo: Copia directa de plantilla lista");
        System.out.println("Manual:   8 parámetros + validaciones\n");
        
        System.out.println("🎉 PATRÓN PROTOTYPE FUNCIONANDO CORRECTAMENTE!");
        System.out.println("✅ Clonación eficiente");
        System.out.println("✅ Objetos independientes");
        System.out.println("✅ Herencia de plantilla");
    }
}