# QA y buenas prácticas para SospechApp

Este documento resume las prácticas recomendadas para mantener la calidad del proyecto y facilitar la colaboración.

## 1. Automatización y CI
- Todos los commits a `main`/`master` deben pasar los tests automáticos (unitarios e instrumentados).
- El workflow de GitHub Actions (`.github/workflows/android-ci.yml`) ejecuta los tests y sube reportes.

## 2. Checklist de QA
- Antes de cada release, sigue el checklist en `QA_CHECKLIST.md`.
- Añade casos específicos si se detectan bugs recurrentes o nuevas funcionalidades críticas.

## 3. Cobertura de tests
- Mantén y amplía los tests unitarios e instrumentados al añadir nuevas pantallas o lógica.
- Cubre casos de error y edge cases.

## 4. ProGuard y seguridad
- Revisa y actualiza `proguard-rules.pro` si añades nuevas librerías o funcionalidades sensibles.
- No dejes logs sensibles en producción.

## 5. Textos y traducciones
- Mantén actualizados los archivos `strings.xml` en todos los idiomas soportados.
- Revisa ortografía y consistencia tras cada cambio relevante.

## 6. Documentación
- Documenta flujos complejos, decisiones de arquitectura y cualquier workaround relevante en este archivo o en el README principal.

---

¿Dudas o sugerencias? Contacta a manuacedevops@gmail.com o abre un issue en el repositorio.

