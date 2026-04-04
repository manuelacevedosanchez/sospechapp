# QA Checklist – SospechApp

Este checklist ayuda a asegurar la calidad mínima antes de cada release o subida a producción. Puedes ampliarlo según evolucione la app.

## 1. Pruebas automáticas
- [ ] Todos los tests unitarios pasan (`./gradlew test`)
- [ ] Todos los tests instrumentados pasan (`./gradlew connectedAndroidTest`)
- [ ] Cobertura de tests revisada y aceptable

## 2. Pruebas manuales
- [ ] Navegación completa por todas las pantallas
- [ ] Configuración de partida y roles funciona correctamente
- [ ] Palabra aleatoria y personalizada funcionan
- [ ] Pantalla de roles muestra información correcta
- [ ] Botones principales (Nueva partida, Cómo se juega, Ajustes, Miscelánea) funcionan
- [ ] Textos y traducciones correctos en español e inglés
- [ ] Política de privacidad accesible y actualizada
- [ ] Animaciones y vibración funcionan si están activadas
- [ ] App no se cierra inesperadamente en ningún flujo

## 3. QA técnica
- [ ] ProGuard configurado y sin warnings críticos
- [ ] Tamaño del APK/AAB razonable
- [ ] No hay logs sensibles en producción
- [ ] No se filtran datos personales
- [ ] Se muestra el formulario de consentimiento de anuncios donde aplica

## 4. Publicación
- [ ] Iconos y capturas actualizados en Play Store
- [ ] Descripciones corta y larga revisadas
- [ ] Versión y número de compilación incrementados

---

Marca cada ítem antes de publicar una nueva versión. Añade casos específicos según evolucione la app o si se detectan bugs recurrentes.

