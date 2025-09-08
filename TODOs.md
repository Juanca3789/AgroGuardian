# ✅ AgroGuardian – ToDo List (Alpha Development, 3 meses)

> ⚠️ Nota: Eliminado QA para acelerar fase alpha. Solo gestión y desarrollo directo.

---

## 🌱 General

* [ ] Configurar **proyecto KMM con Compose Multiplatform** (Android + iOS).
* [ ] Integrar librerías base:
  * [ ] `compose.material3`, `navigation`, `lifecycle`.
  * [ ] `materialKolor` (tematización dinámica).
  * [ ] `SQLDelight` (persistencia multiplataforma).
  * [ ] `Ktor` (sincronización remota cuando haya conexión).
  * [ ] Librerías IA: `TensorFlow Lite`, `llama.cpp`/GGUF.
* [ ] Implementar **AppTheme** con paleta de colores confirmada.
* [ ] Establecer **Navigation Drawer lateral** como esquema principal.
* [ ] Añadir **FAB global 💬** para abrir Chatbot desde cualquier pantalla.

---

## 🏠 Pantalla Inicio / Dashboard

* [ ] Mostrar saludo contextual dinámico.
* [ ] Cards principales:

  * [ ] 📷 **Diagnóstico visual**.
  * [ ] 🌦️ **Clima local**.
  * [ ] 📊 **Estado de parcela**.
* [ ] FAB para abrir Chatbot.

---

## 📷 Diagnóstico Visual (CropGuardian)

* [ ] Integrar cámara (CameraX / AVFoundation).
* [ ] Permitir captura de hoja/planta.
* [ ] Procesar imagen con modelo local (TensorFlow Lite / GGUF).
* [ ] UI: vista previa + resultado + recomendación.
* [ ] Guardar diagnóstico en base local.

---

## 🧮 Planificación Agroecológica (AgriBrain)

* [ ] Formulario de cultivos (actual, anteriores, tipo de suelo).
* [ ] Generar plan rotativo.
* [ ] Mostrar calendario/timeline visual.
* [ ] Guardar planes históricos.

---

## 🌦️ Asistente Climático

* [ ] Conectar con APIs (OpenWeatherMap + NASA POWER).
* [ ] Almacenar datos para consulta offline.
* [ ] UI: timeline 7 días + alertas.
* [ ] Enviar alerta al Chatbot para recomendaciones.

---

## 📊 Estado de Parcela

* [ ] Listar diagnósticos previos.
* [ ] Mostrar prácticas aplicadas.
* [ ] Visualizar con gráficos simples.
* [ ] Exportar resumen CSV/PDF.

---

## 🔁 Economía Circular

* [ ] Mapas con OpenStreetMap/MapLibre.
* [ ] Lista de mercados, cooperativas, puntos de trueque.
* [ ] Publicar excedentes (formulario simple).
* [ ] Buscar herramientas e insumos.
* [ ] Gamificación (badges por participación comunitaria).

---

## 💬 Chatbot Agrónomo

* [ ] FAB global → acceso rápido.
* [ ] UI estilo chat (texto + voz).
* [ ] Entrada de texto + dictado por voz.
* [ ] Respuestas claras + íconos.
* [ ] Soporte multilingüe + traducciones locales.
* [ ] Adjuntar foto para diagnóstico.
* [ ] Arquitectura híbrida: modelo local + remoto.
* [ ] Memoria externa: persistencia de contexto y resúmenes.

---

## 📈 Indicadores de Salud del Suelo

* [ ] Cálculo de índice de regeneración.
* [ ] Visualización tipo semáforo.

---

## 🐛 Seguimiento de Plagas y Enfermedades

* [ ] Registro manual de plagas.
* [ ] Integrar detección por cámara.
* [ ] Alertas comunitarias en región.

---

## 💧 Gestión del Agua

* [ ] Calcular necesidades de riego (evapotranspiración + clima).
* [ ] Registro de riegos.
* [ ] Consejos de eficiencia hídrica.

---

## 👥 Comunidad y Colaboración

* [ ] Foros comunitarios simples.
* [ ] Chat regional.
* [ ] Rankings cooperativas.

---

## 📑 Economía Agrícola

* [ ] Registro de costos e ingresos.
* [ ] Reportes simples PDF/CSV.

---

## 🛰️ Datos Satelitales Ligeros

* [ ] Integrar NDVI básico (mapa coloreado).
* [ ] Mostrar vigor de cultivos (verde/rojo).

---

## 🎓 Módulo Educativo Integrado

* [ ] Microcursos cortos.
* [ ] Recomendaciones adaptadas a cultivos registrados.

---

## 📦 Gestión de Inventarios

* [ ] Registrar semillas, herramientas, bioinsumos.
* [ ] Alertas de agotamiento.

---

## 🚜 Mantenimiento de Maquinaria

* [ ] Recordatorios de mantenimiento preventivo.
* [ ] Guías básicas de uso/mantenimiento.

---

## 🧾 Trazabilidad y Certificaciones

* [ ] Registro de prácticas sostenibles.
* [ ] Generación de reportes para certificación.

---

## 📅 Calendario Agrícola Integrado

* [ ] Vista mensual/semanal de tareas.
* [ ] Recordatorios automáticos.

---

## 📷 Bitácora Multimedia

* [ ] Guardar fotos y notas por fecha.
* [ ] Comparar evolución de cultivos.

---

## ⚖️ Comparador de Prácticas Agrícolas

* [ ] Registrar dos métodos distintos.
* [ ] Comparar resultados en rendimiento, costos y sostenibilidad.

---

## ⚙️ Configuración

* [ ] Preferencias de idioma, tema, conexión.
* [ ] Control de datos (exportar/borrar).
* [ ] Selección de modelo IA (local o descargable).

---

## 📚 Guía AgroGuardian

* [ ] Tutorial inicial paso a paso.
* [ ] Recomendaciones prácticas regenerativas.
* [ ] FAQ básicas.

---

## ℹ️ Acerca de

* [ ] Info del proyecto.
* [ ] Créditos y licencias.
* [ ] Versión de la app.

---

## 🧩 Workflow Técnico

* [ ] Orchestrator: selección de nodos IA.
* [ ] Model Manager: tiny en APK + opción descargable.
* [ ] Memory Manager: resúmenes y persistencia.
* [ ] Sync Manager: sincronización y data commons.

---