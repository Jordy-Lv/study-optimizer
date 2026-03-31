-- Carga inicial de los 30 retos de mouredev.
-- Agrupados en 6 fases de 5 retos cada una, de menor a mayor dificultad.

INSERT INTO exercise (day_number, title, description, phase) VALUES
-- Fase 1: Fundamentos
(1,  'Hola Mundo y comentarios',        'Imprime tu primer mensaje y aprende a comentar el codigo.', 1),
(2,  'Tipos de datos y variables',      'Declara variables de distintos tipos y muestralas.', 1),
(3,  'Operadores y expresiones',        'Practica operadores aritmeticos, logicos y de comparacion.', 1),
(4,  'Condicionales',                   'Usa if/else y switch para tomar decisiones.', 1),
(5,  'Bucles',                          'Recorre datos con for, while y do-while.', 1),

-- Fase 2: Estructuras de datos basicas
(6,  'Cadenas de texto',                'Manipula strings: longitud, mayusculas, subcadenas.', 2),
(7,  'Arrays',                          'Crea y recorre arreglos de tamano fijo.', 2),
(8,  'Listas dinamicas',                'Usa List/ArrayList para colecciones que crecen.', 2),
(9,  'Mapas (HashMap)',                 'Asocia claves con valores y cuenta frecuencias.', 2),
(10, 'Conjuntos (Set)',                 'Elimina duplicados con estructuras de conjunto.', 2),

-- Fase 3: Funciones y modularidad
(11, 'Funciones y parametros',          'Define funciones reutilizables con argumentos.', 3),
(12, 'Recursion',                       'Resuelve problemas que se llaman a si mismos (factorial, Fibonacci).', 3),
(13, 'Ambito y closures',               'Entiende variables locales, globales y captura de contexto.', 3),
(14, 'Manejo de errores',               'Captura y lanza excepciones de forma controlada.', 3),
(15, 'Lectura y escritura de ficheros', 'Lee y escribe datos en archivos.', 3),

-- Fase 4: Programacion orientada a objetos
(16, 'Clases y objetos',                'Modela entidades con atributos y metodos.', 4),
(17, 'Encapsulamiento',                 'Protege el estado con getters/setters y visibilidad.', 4),
(18, 'Herencia',                        'Reutiliza comportamiento entre clases.', 4),
(19, 'Polimorfismo e interfaces',       'Trata distintos objetos de forma uniforme.', 4),
(20, 'Composicion vs herencia',         'Decide cuando componer en lugar de heredar.', 4),

-- Fase 5: Algoritmos
(21, 'Busqueda lineal y binaria',       'Encuentra elementos en colecciones ordenadas y no ordenadas.', 5),
(22, 'Ordenamiento',                    'Implementa y compara algoritmos de ordenacion.', 5),
(23, 'Complejidad algoritmica (Big-O)', 'Analiza el costo en tiempo y espacio.', 5),
(24, 'Pilas y colas',                   'Usa estructuras LIFO y FIFO.', 5),
(25, 'Arboles basicos',                 'Recorre y construye arboles binarios.', 5),

-- Fase 6: Avanzado y funcional
(26, 'Programacion funcional',          'Usa funciones de orden superior (map, filter, reduce).', 6),
(27, 'Streams',                         'Procesa colecciones con la API de Streams.', 6),
(28, 'Expresiones lambda',              'Escribe funciones anonimas concisas.', 6),
(29, 'Genericos',                       'Escribe codigo reutilizable y con tipos seguros.', 6),
(30, 'Proyecto final integrador',       'Combina todo en una pequena aplicacion completa.', 6);
