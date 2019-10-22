SELECT actualDate, companySymbol, concept, report FROM apiadbossDB.qualityTest
WHERE actualDate = '2019-07-10'
GROUP BY actualDate, concept, report, companySymbol
