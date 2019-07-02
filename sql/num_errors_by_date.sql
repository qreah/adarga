SELECT actualDate, COUNT(companySymbol) AS PosibleErrors FROM apiadbossDB.qualityTest
GROUP BY actualDate
ORDER BY actualDate DESC;