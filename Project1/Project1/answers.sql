/*Query 1*/
SELECT name AS name
FROM   movies
WHERE  gen_id = 1
AND    release_year = 2016;

/*Query 2*/
SELECT   m.name        AS Name,
         g.title       AS Genre,
         m.description AS Description,
         Avg(r.star)   AS Ratings
FROM     movies m
JOIN     ratings r
ON       m.id = r.mid
JOIN     genres g
ON       m.gen_id = g.id
GROUP BY r.mid
ORDER BY Avg(r.star) DESC
LIMIT    1;

/*Query 3*/
SELECT   name        AS name,
         title       AS title,
         description AS description
FROM     ticket_sell t
JOIN     movies m
ON       t.mid = m.id
JOIN     genres g
ON       m.gen_id = g.id
GROUP BY t.mid
HAVING   Count(t.tid) =
         (
                  SELECT   Count(t.tid)
                  FROM     ticket_sell t
                  JOIN     movies m
                  ON       t.mid = m.id
                  JOIN     genres g
                  ON       m.gen_id = g.id
                  GROUP BY t.mid
                  ORDER BY Count(t.tid) DESC
                  LIMIT    1);
/*Query 4*/
SELECT   th.name      AS name,
         count(t.tid) AS num_of_movies
FROM     ticket_sell t
JOIN     movies m
ON       t.mid = m.id
JOIN     theatres th
ON       th.id = t.tid
GROUP BY t.tid
ORDER BY count(t.tid) DESC;
         name
/*Query 5*/
SELECT   mc.name AS Name
FROM     movie_cast mc
JOIN     ticket_sell ts
ON       ts.mid = mc.mid
GROUP BY mc.act_id
HAVING   sum(ts.price) =
         (
                  SELECT   sum(ts.price)
                  FROM     movie_cast mc
                  JOIN     ticket_sell ts
                  ON       ts.mid = mc.mid
                  GROUP BY mc.act_id
                  ORDER BY sum(ts.price) DESC
                  LIMIT    1);

/*Query 6*/
SELECT th.name             AS Name,
       Date(t1.start_time) AS Day
FROM   ticket_sell t1
JOIN   ticket_sell t2
ON     t1.tid = t2.tid
JOIN   theatres th
ON     th.id = t1.tid
WHERE  t1.mid != t2.mid
AND    Date(t1.start_time) = Date(t2.start_time)
AND    t1.mid = 1
AND    t2.mid = 4;

/*Query 7*/
UPDATE theatres
SET    address = "27 Broad Ave",
       zip = 47943
WHERE  `name` = "Lake Shore";

/*Query 8*/
update movies
SET    length = length -20
WHERE  release_year = 2016;
/*Query 9*/
DELETE
FROM   ratings
WHERE  star < 2.0
AND    year(rev_date) = 2019;
/*Query 10*/
DELETE
FROM   movies
WHERE  id IN
       (
              SELECT id
              FROM   (
                              SELECT   id
                              FROM     movies m
                              JOIN     ratings r
                              ON       r.mid = m.id
                              GROUP BY r.mid
                              HAVING   avg(r.star) < 3.5)t);