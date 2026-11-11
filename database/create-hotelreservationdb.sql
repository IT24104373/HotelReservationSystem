IF DB_ID(N'HotelReservationDB') IS NULL
BEGIN
    CREATE DATABASE HotelReservationDB;
END;
GO

USE HotelReservationDB;
GO

-- Older versions of this project used dbo.Users(UserId, Username, Email, Password).
-- Keep that data, but move the legacy table out of the application's table name.
IF OBJECT_ID(N'dbo.Users', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.Users', N'id') IS NULL
   AND OBJECT_ID(N'dbo.LegacyUsers', N'U') IS NULL
BEGIN
    EXEC sp_rename N'dbo.Users', N'LegacyUsers';
END;
GO

IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(255) NULL,
        email NVARCHAR(255) NULL,
        password NVARCHAR(255) NULL,
        role NVARCHAR(255) NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.customers', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.customers (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(255) NULL,
        email NVARCHAR(255) NULL,
        password NVARCHAR(255) NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.hotels', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.hotels (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(255) NULL,
        location NVARCHAR(255) NULL,
        price_per_night BIGINT NOT NULL,
        owner_id BIGINT NULL,
        image_url NVARCHAR(1000) NULL,
        description NVARCHAR(2000) NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.bookings', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bookings (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        customer_id BIGINT NULL,
        hotel_id BIGINT NULL,
        hotel_name NVARCHAR(255) NULL,
        check_in_date DATE NULL,
        check_out_date DATE NULL,
        guest_count INT NULL,
        total_price BIGINT NULL,
        status NVARCHAR(255) NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.reviews', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.reviews (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        customer_id BIGINT NULL,
        customer_name NVARCHAR(255) NULL,
        hotel_id BIGINT NULL,
        hotel_name NVARCHAR(255) NULL,
        rating INT NOT NULL,
        comment NVARCHAR(1000) NULL,
        review_date DATE NULL
    );
END;
GO

-- Demo rows are optional. The application seeder also creates them when tables are empty.
IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'owner@hotel.com')
BEGIN
    INSERT INTO dbo.users (name, email, password, role)
    VALUES (N'Nimal Fernando', N'owner@hotel.com', N'123456', N'OWNER');
END;

IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'customer@hotel.com')
BEGIN
    INSERT INTO dbo.users (name, email, password, role)
    VALUES (N'Kamal Perera', N'customer@hotel.com', N'123456', N'CUSTOMER');
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.hotels)
BEGIN
    DECLARE @OwnerId BIGINT = (SELECT TOP 1 id FROM dbo.users WHERE email = N'owner@hotel.com');
    DECLARE @CustomerId BIGINT = (SELECT TOP 1 id FROM dbo.users WHERE email = N'customer@hotel.com');

    INSERT INTO dbo.hotels (name, location, price_per_night, owner_id, image_url, description)
    VALUES
    (N'Amanwella Ocean Sanctuary', N'Tangalle, Southern Province', 45000, @OwnerId,
     N'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80',
     N'Set among mature coconut groves facing a crescent-shaped beach, Amanwella is a contemporary beach resort offering private plunge pools and uninterrupted ocean panoramas.'),
    (N'Heritance Kandalama Forest Retreat', N'Dambulla, Cultural Triangle', 38500, @OwnerId,
     N'https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1200&q=80',
     N'An architectural masterpiece designed by Geoffrey Bawa, nestled beneath a cliff face overlooking Kandalama Lake, surrounded by lush tropical biodiversity.'),
    (N'The Grand Heritage Resort', N'Nuwara Eliya, Central Highlands', 28000, @OwnerId,
     N'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1200&q=80',
     N'Experience colonial elegance in the misty hills of Little England. Featuring manicured rose gardens, warm log fires, high tea pavilions, and award-winning dining.'),
    (N'Cape Weligama Luxury Cliff Villas', N'Weligama Bay, Southern Coast', 52000, @OwnerId,
     N'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1200&q=80',
     N'Perched atop a rugged cliff towering 40 meters above the Indian Ocean, featuring iconic crescent-shaped infinity pools, bespoke butler service, and private dining.');

    DECLARE @HotelId BIGINT = (SELECT TOP 1 id FROM dbo.hotels ORDER BY id);
    DECLARE @HotelName NVARCHAR(255) = (SELECT TOP 1 name FROM dbo.hotels ORDER BY id);

    INSERT INTO dbo.bookings
        (customer_id, hotel_id, hotel_name, check_in_date, check_out_date, guest_count, total_price, status)
    VALUES
        (@CustomerId, @HotelId, @HotelName, DATEADD(DAY, 3, CAST(GETDATE() AS DATE)),
         DATEADD(DAY, 6, CAST(GETDATE() AS DATE)), 1, 135000, N'BOOKED');

    INSERT INTO dbo.reviews
        (customer_id, customer_name, hotel_id, hotel_name, rating, comment, review_date)
    VALUES
        (@CustomerId, N'Kamal Perera', @HotelId, @HotelName, 5,
         N'Outstanding ocean views, incredible infinity pool, and world-class hospitality! Highly recommended.',
         CAST(GETDATE() AS DATE));
END;
GO

-- Verify the objects and rows created in SSMS.
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = N'dbo'
  AND TABLE_NAME IN (N'users', N'customers', N'hotels', N'bookings', N'reviews')
ORDER BY TABLE_NAME;

SELECT
    (SELECT COUNT(*) FROM dbo.users) AS users_count,
    (SELECT COUNT(*) FROM dbo.customers) AS customers_count,
    (SELECT COUNT(*) FROM dbo.hotels) AS hotels_count,
    (SELECT COUNT(*) FROM dbo.bookings) AS bookings_count,
    (SELECT COUNT(*) FROM dbo.reviews) AS reviews_count;
GO
