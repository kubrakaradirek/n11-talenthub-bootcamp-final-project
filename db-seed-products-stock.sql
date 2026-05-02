BEGIN;

INSERT INTO products (id, brand, category, color, description, img, price, title) VALUES
(1, 'Apple', 'Elektronik', 'Uzay Grisi', 'iPhone 15 Pro 128GB Akıllı Telefon', 'https://n11scdn.akamaized.net/a1/640/15/87/64/34/IMG-4085759790616553134.jpg', 75000, 'iPhone 15 Pro'),
(2, 'Samsung', 'Elektronik', 'Siyah', 'Galaxy S24 Ultra 256GB', 'https://n11scdn.akamaized.net/a1/320_480/16/53/96/43/IMG-2556244822046481376.jpg', 68000, 'Galaxy S24 Ultra'),
(3, 'Sony', 'Elektronik', 'Gümüş', 'WH-1000XM5 Gürültü Engelleyici Kulaklık', 'https://n11scdn.akamaized.net/a1/320_480/01/82/52/74/IMG-2651796578724290647.jpg', 12500, 'Sony Bluetooth Kulaklık'),
(4, 'Dyson', 'Ev Aletleri', 'Mor', 'V15 Detect Kablosuz Süpürge', 'https://n11scdn.akamaized.net/a1/320_480/13/30/24/98/IMG-3678528436007527671.jpg', 28000, 'Dyson V15 Süpürge'),
(5, 'Philips', 'Mutfak Aletleri', 'Siyah', 'Airfryer XXL Fritöz', 'https://n11scdn.akamaized.net/a1/320_480/10/25/05/01/IMG-2810749935846496243.jpg', 6500, 'Philips Airfryer'),
(6, 'Logitech', 'Elektronik', 'Gri', 'MX Master 3S Kablosuz Mouse', 'https://n11scdn.akamaized.net/a1/320_480/06/60/37/45/IMG-1179273276792747956.jpg', 3800, 'Logitech MX Master'),
(7, 'Apple', 'Elektronik', 'Gümüş', 'MacBook Air M3 13 inç 16GB 512GB', 'https://n11scdn.akamaized.net/a1/320_480/16/38/36/72/IMG-3786392769083416166.jpg', 52000, 'MacBook Air M3'),
(8, 'JBL', 'Elektronik', 'Kırmızı', 'Flip 6 Taşınabilir Hoparlör', 'https://n11scdn.akamaized.net/a1/640/02/05/76/28/IMG-246626775428902936.jpg', 3200, 'JBL Flip 6'),
(9, 'Bosch', 'Mutfak Aletleri', 'Beyaz', 'Tam Otomatik Kahve Makinesi', 'https://n11scdn.akamaized.net/a1/320_480/10/92/58/44/IMG-5241696210405177357.jpg', 18500, 'Bosch Kahve Makinesi'),
(10, 'Xiaomi', 'Elektronik', 'Beyaz', 'Mi Electric Scooter Pro 2', 'https://n11scdn.akamaized.net/a1/320_480/10/14/66/98/IMG-7552431732053957134.jpg', 16500, 'Xiaomi Scooter'),
(11, 'Dell', 'Elektronik', 'Gri', 'XPS 13 9315 Laptop', 'https://n11scdn.akamaized.net/a1/320_480/08/10/70/58/IMG-1856258878903223843.webp', 48000, 'Dell XPS 13'),
(12, 'Razer', 'Elektronik', 'RGB', 'BlackWidow V4 Oyuncu Klavyesi', 'https://n11scdn.akamaized.net/a1/320_480/15/38/88/91/IMG-5061025100237267194.jpg', 7200, 'Razer Klavye'),
(13, 'Canon', 'Elektronik', 'Siyah', 'EOS R6 Mark II Gövde', 'https://n11scdn.akamaized.net/a1/320_480/08/88/53/85/IMG-5279614600065558834.jpg', 92000, 'Canon Kamera'),
(14, 'Oral-B', 'Elektronik', 'Mavi', 'iO Series 9 Elektrikli Diş Fırçası', 'https://n11scdn.akamaized.net/a1/320_480/12/77/55/22/IMG-3823838090383962482.jpg', 8500, 'Oral-B iO 9'),
(15, 'Nespresso', 'Mutfak Aletleri', 'Siyah', 'Vertuo Pop Kahve Makinesi', 'https://n11scdn.akamaized.net/a1/320_480/05/52/27/11/IMG-6414334905297760019.jpg', 4800, 'Nespresso Vertuo'),
(16, 'Nintendo', 'Elektronik', 'Kırmızı-Mavi', 'Switch OLED Konsol', 'https://n11scdn.akamaized.net/a1/320_480/10/68/82/82/IMG-7807356811448487278.jpg', 14500, 'Nintendo Switch'),
(17, 'Tefal', 'Ev Aletleri', 'Kırmızı', 'Ultimate Pure Buharlı Ütü', 'https://n11scdn.akamaized.net/a1/320_480/06/39/70/89/IMG-293737529739134261.jpg', 3900, 'Tefal Ütü'),
(18, 'Samsung', 'Elektronik', 'Siyah', '55 Inç 4K Crystal UHD TV', 'https://n11scdn.akamaized.net/a1/320_480/01/44/23/95/IMG-6938222030745987559.jpg', 24500, 'Samsung Smart TV'),
(19, 'HP', 'Elektronik', 'Beyaz', 'LaserJet Pro Yazıcı', 'https://n11scdn.akamaized.net/a1/320_480/16/64/48/24/IMG-4551473736481630019.jpg', 6200, 'HP LaserJet'),
(20, 'Kindle', 'Elektronik', 'Lacivert', 'Paperwhite 16GB E-Kitap Okuyucu', 'https://n11scdn.akamaized.net/a1/320_480/12/70/81/23/IMG-6677674913843430260.jpg', 5900, 'Kindle Paperwhite'),
(21, 'Roborock', 'Ev Aletleri', 'Beyaz', 'S8 Sonic Mopping Robot Süpürge', 'https://n11scdn.akamaized.net/a1/640/02/87/66/04/IMG-7837658994431554019.jpg', 22000, 'Roborock S8'),
(22, 'LG', 'Elektronik', 'Gri', '27 Inç UltraGear Gaming Monitör', 'https://n11scdn.akamaized.net/a1/320_480/04/99/92/41/IMG-2506850955593631799.jpg', 11500, 'LG Monitör'),
(23, 'Siemens', 'Mutfak Aletleri', 'Inox', 'iQ300 Bulaşık Makinesi', 'https://n11scdn.akamaized.net/a1/320_480/02/37/99/00/IMG-4770168510166722760.jpg', 19000, 'Siemens Bulaşık Makinesi'),
(24, 'Arçelik', 'Ev Aletleri', 'Beyaz', '9 KG Çamaşır Makinesi', 'https://n11scdn.akamaized.net/a1/320_480/08/34/59/85/IMG-2648699206106858465.jpg', 17500, 'Arçelik Çamaşır Makinesi')
ON CONFLICT (id) DO UPDATE SET
    brand = EXCLUDED.brand,
    category = EXCLUDED.category,
    color = EXCLUDED.color,
    description = EXCLUDED.description,
    img = EXCLUDED.img,
    price = EXCLUDED.price,
    title = EXCLUDED.title;

SELECT setval(pg_get_serial_sequence('products', 'id'), (SELECT MAX(id) FROM products));

INSERT INTO product_stock (product_id, available_quantity, product_name, reserved_quantity) VALUES
(1, 50, 'iPhone 15 Pro', 0),
(2, 30, 'Galaxy S24 Ultra', 0),
(3, 10, 'Sony Bluetooth Kulaklık', 0),
(4, 40, 'Dyson V15 Süpürge', 0),
(5, 50, 'Philips Airfryer', 0),
(6, 60, 'Logitech MX Master', 0),
(7, 70, 'MacBook Air M3', 0),
(8, 80, 'JBL Flip 6', 0),
(9, 25, 'Bosch Kahve Makinesi', 0),
(10, 15, 'Xiaomi Scooter', 0),
(11, 20, 'Dell XPS 13', 0),
(12, 35, 'Razer Klavye', 0),
(13, 8, 'Canon Kamera', 0),
(14, 55, 'Oral-B iO 9', 0),
(15, 45, 'Nespresso Vertuo', 0),
(16, 18, 'Nintendo Switch', 0),
(17, 90, 'Tefal Ütü', 0),
(18, 22, 'Samsung Smart TV', 0),
(19, 33, 'HP LaserJet', 0),
(20, 75, 'Kindle Paperwhite', 0),
(21, 14, 'Roborock S8', 0),
(22, 28, 'LG Monitör', 0),
(23, 12, 'Siemens Bulaşık Makinesi', 0),
(24, 10, 'Arçelik Çamaşır Makinesi', 0)
ON CONFLICT (product_id) DO UPDATE SET
    available_quantity = EXCLUDED.available_quantity,
    product_name = EXCLUDED.product_name,
    reserved_quantity = EXCLUDED.reserved_quantity;

COMMIT;

SELECT COUNT(*) AS products_count FROM products;
SELECT COUNT(*) AS product_stock_count FROM product_stock;
