# Performance Report — Smart Inventory

## 1. Introducción

Este documento analiza la complejidad algorítmica de cada endpoint del sistema e incluye mediciones empíricas obtenidas mediante el endpoint `GET /api/admin/performance-report`, que utiliza `System.nanoTime()` para medir tiempos reales con datasets de **1 000**, **10 000** y **100 000** registros.

El almacenamiento subyacente es un `ConcurrentHashMap<ID, T>`, cuya función de dispersión permite acceso, inserción y eliminación en tiempo **O(1) amortizado**. Las operaciones que necesitan recorrer toda la colección son inherentemente **O(n)**.

---
