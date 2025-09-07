# ✅ AgroGuardian – ToDo List (Workflow + Pantallas)

## 🌱 General
- [✅] Configurar **proyecto KMM con Compose Multiplatform** (Android + iOS).
- [➖] Integrar librerías base:
    - [ ] `compose.material3`, `navigation`, `lifecycle`.
    - [✅] `materialKolor` (tematización dinámica).
    - [ ] `SQLDelight` (persistencia multiplataforma).
    - [ ] `Ktor` (sincronización remota cuando haya conexión).
- [✅] Implementar **AppTheme** con colores:
    - Primary: Verde hoja 🌿 (#388E3C).
    - Secondary: Turquesa 💧 (#009688).
    - Tertiary: Tierra 🌾 (#8D6E63).
    - Neutral: generados por MaterialKolor.
- [ ] Establecer **Navigation Drawer lateral** como esquema principal de navegación.
- [ ] Añadir **FAB global 💬** para abrir Chatbot desde cualquier pantalla.

---

## 🏠 Pantalla Inicio / Dashboard
- [ ] Mostrar saludo contextual (ej. “Buenos días, Juan 🌞”).
- [ ] Cards principales:
    - [ ] 📷 **Diagnóstico visual** → acceso directo a cámara.
    - [ ] 🌦️ **Clima local** → resumen de próximos días.
    - [ ] 📊 **Estado de parcela** → prácticas recientes + alertas.
- [ ] FAB: acción rápida para abrir Chatbot.

---

## 📷 Diagnóstico Visual (CropGuardian)
- [ ] Integrar cámara (CameraX en Android, AVFoundation en iOS).
- [ ] Permitir captura de hoja/planta.
- [ ] Procesar imagen con modelo local (TensorFlow Lite o GGUF vía llama.cpp).
- [ ] Mostrar:
    - Vista previa de foto.
    - Resultado (ej. “Posible plaga: gusano cogollero”).
    - Recomendación inmediata (ej. “Aplicar biofertilizante en 3 días”).
- [ ] Guardar diagnóstico en base local (SQLDelight).

---

## 🧮 Planificación Agroecológica (AgriBrain)
- [ ] Formulario simple:
    - Cultivo actual (selector con íconos: 🌽 ☕ 🌾).
    - Últimos cultivos sembrados (lista).
    - Tipo de suelo (manual o detectado por GPS/registro).
- [ ] Generar plan rotativo:
    - Mostrar calendario estilo timeline (ej. “Mes 1–3: maíz 🌽 / Mes 4–6: frijol 🌱”).
- [ ] Guardar y consultar planes históricos.

---

## 🌦️ Asistente Climático
- [ ] Conectar con APIs abiertas (NASA, Copernicus, FAO) vía Ktor.
- [ ] Almacenar datos para consulta offline.
- [ ] UI:
    - Timeline próximos 7 días con iconos ☀️ 🌧️ ❄️.
    - Tarjetas de alerta:
        - “⚠️ Helada probable en 3 días.”
        - “💧 Lluvia fuerte mañana, revise drenajes.”
- [ ] Opción de enviar alerta al Chatbot → “qué hacer frente a esta situación”.

---

## 📊 Estado de Parcela
- [ ] Listar diagnósticos previos.
- [ ] Mostrar prácticas aplicadas (ej. riego, fertilización).
- [ ] Visualización: gráfico simple (rendimiento, rotación aplicada).
- [ ] Posibilidad de exportar resumen (CSV / PDF).

---

## 🔁 Economía Circular
- [ ] Lista de mercados y cooperativas cercanas.
- [ ] Opciones:
    - Publicar excedente 🌽 (formulario simple: qué, cuánto, precio).
    - Buscar herramientas → listado por categorías.
- [ ] Implementar gamificación:
    - Badges por trueques, compost comunitario, participación en ferias.

---

## 💬 Chatbot Agrónomo
- [ ] FAB global que abre chat desde cualquier pantalla.
- [ ] UI estilo chat (similar a WhatsApp).
- [ ] Entrada de texto + botón de dictado por voz.
- [ ] Respuestas:
    - Texto + iconos pequeños.
    - Traducción a idiomas locales.
    - Capacidad de adjuntar foto para diagnóstico.
- [ ] Arquitectura híbrida:
    - Modelo local ligero (GGUF/TFLite) siempre disponible.
    - Si hay conexión estable, usar modelo remoto más grande.
- [ ] Integrar **memoria externa**:
    - Recordar datos persistentes (cultivo, suelo, clima local).
    - Resumir interacciones largas para mantener contexto sin sobrecargar el dispositivo.

---

## ⚙️ Configuración
- [ ] Preferencias: idioma, tema (oscuro/claro), conexión (offline/online).
- [ ] Control de datos → permitir exportar/borrar información personal.
- [ ] Selección de modelo IA (tiny local o extendido descargable).

---

## 📚 Guía AgroGuardian
- [ ] Tutorial inicial paso a paso.
- [ ] Recomendaciones sobre prácticas regenerativas.
- [ ] FAQ básicas (ej. “cómo tomar foto para diagnóstico”).

---

## ℹ️ Acerca de
- [ ] Info del proyecto, versión de la app.
- [ ] Créditos (FAO, ONGs, universidades, desarrolladores).
- [ ] Licencia (open source).

---

## 🧩 Workflow Técnico
- [ ] **Orchestrator** (commonMain):
    - Selecciona nodos según query (ej. diagnóstico → modelo visión + LLM local).
    - Si hay red → extiende workflow con modelo remoto.
- [ ] **Model Manager**:
    - APK incluye modelo tiny.
    - Opción de descargar modelo más grande vía WiFi.
- [ ] **Memory Manager**:
    - Guardar contexto a corto/largo plazo.
    - Usar resúmenes como contexto para LLM pequeño.
- [ ] **Sync Manager**:
    - Cuando hay conexión, sincronizar diagnósticos y planes con nube.
    - Compartir datos anonimizados en “data commons” rurales.

---

## 🔬 QA y CI/CD
- [ ] Configurar **tests unitarios** en `commonTest`.
- [ ] Tests de UI con Compose Preview y Espresso/KMP.
- [ ] Pipeline CI (GitHub Actions o GitLab CI):
    - Compilación Android e iOS.
    - Lint + Detekt.
    - Generación de APK/AAB y .ipa para pruebas.
- [ ] Distribución en canales cerrados (ej. Firebase App Distribution, TestFlight).