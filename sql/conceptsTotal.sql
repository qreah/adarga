SELECT concept, COUNT(concept) AS num, report FROM apiadbossDB.conceptsTable
WHERE concept LIKE '%cash%'
GROUP BY concept, report
ORDER BY num DESC
