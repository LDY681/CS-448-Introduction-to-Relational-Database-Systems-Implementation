INSERT INTO Genres VALUES(1, 'Action');
INSERT INTO Genres VALUES(2, 'Comedy');
INSERT INTO Genres VALUES(3, 'Horror');
INSERT INTO Genres VALUES(4, 'Mystery');
INSERT INTO Genres VALUES(5, 'Historical');
INSERT INTO Genres VALUES(6, 'Drama');


INSERT INTO Movies VALUES(1, 'The Shawshank Redemption', 6, 'Two imprisoned men', 142, 1994);
INSERT INTO Movies VALUES(2, 'The Dark Knight', 1, 'Batman, Joker', 152, 2008);
INSERT INTO Movies VALUES(3, 'Inception', 1, 'Dream-sharing', 148, 2010);
INSERT INTO Movies VALUES(4, 'Dangal', 1, 'Commonwealth Games', 181, 2016);
INSERT INTO Movies VALUES(5, 'La La Land', 2, 'Musical, Love', 138, 2016);


INSERT INTO Theatres VALUES(1, 'Lake Shore', 'N Main St', 47960);
INSERT INTO Theatres VALUES(2, 'Fountain Stone', '1376 St Gaspar Dr', 47978);
INSERT INTO Theatres VALUES(3, 'Regal Village', '2222 E 146th St', 46033);
INSERT INTO Theatres VALUES(4, 'Xscape', '9471 Colerain Ave', 45251);


INSERT INTO Ratings VALUES(1, 1, 4.5, '2016-01-22');
INSERT INTO Ratings VALUES(1, 2, 3.9, '2010-08-09');
INSERT INTO Ratings VALUES(2, 4, 2.3, '2019-05-10');
INSERT INTO Ratings VALUES(3, 1, 5.0, '2013-09-01');
INSERT INTO Ratings VALUES(3, 3, 3.6, '2019-11-23');
INSERT INTO Ratings VALUES(3, 5, 4.0, '2017-04-18');
INSERT INTO Ratings VALUES(4, 3, 4.2, '2016-10-29');
INSERT INTO Ratings VALUES(5, 1, 4.3, '2017-09-20');
INSERT INTO Ratings VALUES(5, 5, 1.9, '2019-03-09');


INSERT INTO Movie_Cast VALUES(1, 5, 'E. Stone', 'Mia');
INSERT INTO Movie_Cast VALUES(2, 5, 'T. Lissauer', 'Valet');
INSERT INTO Movie_Cast VALUES(4, 3, 'E. Page', 'Ariadne');
INSERT INTO Movie_Cast VALUES(5, 1, 'M. Freeman', 'Red');
INSERT INTO Movie_Cast VALUES(6, 1, 'T. Robbins', 'Andy');
INSERT INTO Movie_Cast VALUES(5, 2, 'M. Freeman', 'Lucius');


INSERT INTO Ticket_Sell VALUES(1, 1, 1, '2020-01-06 11:00:00', 8.5, 'Standard');
INSERT INTO Ticket_Sell VALUES(2, 5, 3, '2020-01-09 15:30:00', 10.0, '3D');
INSERT INTO Ticket_Sell VALUES(3, 1, 2, '2020-01-07 14:00:00', 9.5, 'Standard');
INSERT INTO Ticket_Sell VALUES(4, 5, 3, '2020-01-04 19:45:00', 8.5, '3D');
INSERT INTO Ticket_Sell VALUES(5, 2, 4, '2020-01-07 12:30:00', 11.5, 'IMAX');
INSERT INTO Ticket_Sell VALUES(6, 1, 1, '2020-01-06 13:20:00', 8.5, 'Standard');
INSERT INTO Ticket_Sell VALUES(7, 4, 2, '2020-01-07 16:30:00', 9.5, 'Standard');
INSERT INTO Ticket_Sell VALUES(8, 2, 2, '2020-01-06 17:00:00', 10.0, '3D');
INSERT INTO Ticket_Sell VALUES(9, 5, 4, '2020-01-07 14:00:00', 8.5, 'Standard');
