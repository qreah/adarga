SELECT concept, COUNT(concept) AS num FROM apiadbossDB.conceptsTable
GROUP BY concept
ORDER BY num DESC
