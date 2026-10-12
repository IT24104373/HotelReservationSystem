# 🏨 Hotel Reservation System (Campus OOP Project)

Java, Spring Boot, Thymeleaf, Tailwind CSS සහ SQL Database (MySQL / H2) භාවිතයෙන් සාදන ලද විශ්වවිද්‍යාල OOP ව්‍යාපෘතියකි (University Group Project).

---

## 👥 ළමුන් තිදෙනා අතර කාර්යය බෙදීම (Work Breakdown for 3 Students)

මෙම ව්‍යාපෘතිය සිසුන් තිදෙනෙකුට (3 Members) පහසුවෙන් viva presentation එකකදී පැහැදිලි කිරීමට හැකි වන පරිදි වෙන වෙනම Module 3 කට සහ සම්පූර්ණ **CRUD Operations** සහිතව නිර්මාණය කර ඇත:

---

### 👤 Member 1: Hotel Property Management (හෝටල් කළමනාකරණය)
* **අදාළ Classes/Files:**
  - `model/Hotel.java`
  - `repository/HotelRepository.java`
  - `service/HotelService.java`
  - `controller/OwnerController.java`
  - `templates/owner_dashboard.html`
* **CRUD Operations:**
  - **[C] Create:** හෝටල් හිමිකරු විසින් නව හෝටලයක් පද්ධතියට ඇතුළත් කිරීම (`/owner/add-hotel`).
  - **[R] Read:** ලියාපදිංචි කර ඇති සියලු හෝටල් සහ ඒවායේ විස්තර නැරඹීම (`/owner/dashboard`, `/`).
  - **[U] Update:** හෝටලයේ නම, මිල, පිහිටීම, ඡායාරූපය සහ විස්තර Edit/Update කිරීම (`/owner/update-hotel`).
  - **[D] Delete:** හෝටලයක් පද්ධතියෙන් ඉවත් කිරීම (`/owner/delete-hotel`).
* **OOP යෙදීම:**
  - **Encapsulation:** Hotel model හි සියලු fields `private` කර getters/setters මඟින් ලබාදීම සහ අවම මිල LKR 1000 වීමේ validation.
  - **Polymorphism (Method Overloading):** `HotelService` තුළ `getHotels()` සහ `getHotels(Long ownerId)` ලෙස methods overload කර තිබීම.

---

### 👤 Member 2: Reservation & Booking Management (වෙන්කිරීම් කළමනාකරණය)
* **අදාළ Classes/Files:**
  - `model/Booking.java`
  - `repository/BookingRepository.java`
  - `service/HotelService.java` (Booking methods)
  - `controller/CustomerController.java`
  - `templates/customer_dashboard.html`
* **CRUD Operations:**
  - **[C] Create:** හෝටලයක් සඳහා Check-in සහ Check-out දින තෝරා වෙන්කිරීමක් සිදු කිරීම (`/customer/book`).
  - **[R] Read:** පාරිභෝගිකයා විසින් සිදු කරන ලද සියලුම Bookings ලැයිස්තුව සහ තත්ත්වය නැරඹීම (`/customer/dashboard`).
  - **[U] Update:** කලින් වෙන්කළ Booking එකක දින Reschedule / Update කිරීම (`/customer/update-booking`).
  - **[D] Delete / Cancel:** වෙන්කිරීමක් Cancel කිරීම (`/customer/cancel-booking`).
* **OOP යෙදීම:**
  - **Encapsulation:** දින පරාසයන් වල වලංගුභාවය (Check-out > Check-in) සහ Overlapping Bookings පරීක්ෂාව.
  - **Business Logic Isolation:** Repository හරහා දත්ත හැසිරවීම.
* Reservation එක session එකේ authenticated customer ට පමණක් update/cancel කළ හැකි අතර booking එකේ guest count හා booking අවස්ථාවේ මිලද ගබඩා වේ.
* Availability පරීක්ෂාව hotel මට්ටමින් ක්‍රියා කරයි. වෙනම room model එකක් නොමැති නිසා මෙය hotel එකේ සියලු rooms වෙනුවෙන් එක booking එකක් පමණක් එකවර ඉඩදෙන සරල සීමාවකි; individual room availability ලෙස නොසලකන්න.

---

### 👤 Member 3: Review & Rating Management (ශ්‍රේණිගත කිරීම් සහ සමාලෝචන කළමනාකරණය)
* **අදාළ Classes/Files:**
  - `model/Review.java`
  - `repository/ReviewRepository.java`
  - `service/ReviewService.java`
  - `controller/ReviewController.java`
  - `templates/customer_dashboard.html` (Review Section)
* **CRUD Operations:**
  - **[C] Create:** හෝටලයක් සඳහා තරු 1-5 දක්වා Rating එකක් සහ Comment එකක් ලබාදීම (`/review/add`).
  - **[R] Read:** තමා ලබාදුන් සියලුම Reviews සහ ඒවායේ දිනයන් නැරඹීම (`/customer/dashboard#my-reviews`).
  - **[U] Update:** ලබාදුන් Review එකෙහි Rating හෝ Comment එක Edit/Update කිරීම (`/review/update`).
  - **[D] Delete:** ලබාදුන් Review එකක් ඉවත් කිරීම (`/review/delete`).
* **OOP යෙදීම:**
  - **Encapsulation:** Rating අගය 1 ත් 5 ත් අතර පමණක් සීමා කිරීම (`setRating` validation).
  - **Abstraction:** `ReviewRepository` interface එක Spring Data JPA හරහා Abstraction පෙන්වීම.

---

## 🏛️ OOP Concepts (Object-Oriented Programming) යොදාගත් ආකාරය

| OOP Concept | Project එකේ යොදා ඇති ආකාරය | අදාළ Classes / Code |
| :--- | :--- | :--- |
| **Encapsulation** | සියලු Model classes වල attributes `private` කර public Getters & Setters සහ validations (මිල, තරු ගණන, දින) හරහා දත්ත ආරක්ෂා කිරීම. | `Hotel.java`, `Booking.java`, `Review.java`, `User.java` |
| **Inheritance** | `AbstractUser` මවු පන්තියෙන් (Parent Class) `Customer`, `HotelOwner`, `User` දරු පන්ති (Child Classes) බිහිවීම. | `AbstractUser.java` ➡️ `Customer.java`, `HotelOwner.java` |
| **Polymorphism (Overriding)** | `AbstractUser` හි ඇති `getRole()` abstract method එක එක් එක් child class එක තුළ override කර අදාළ role එක ලබාදීම. | `@Override public String getRole()` |
| **Polymorphism (Overloading)** | එකම නම සහිත methods වෙනස් parameters සහිතව භාවිතා කිරීම. | `getHotels()` සහ `getHotels(Long ownerId)` |
| **Abstraction** | `AbstractUser` abstract class එක මඟින් අත්‍යවශ්‍ය method signatures පමණක් අර්ථ දැක්වීම සහ JPA Repository interfaces. | `AbstractUser.java`, `HotelRepository.java`, `ReviewRepository.java` |

---

## 🗄️ SQL Database Configuration

මෙම project එක **MySQL Database** (XAMPP / MySQL Workbench) හෝ **H2 Embedded SQL Database** යන දෙකටම සහය දක්වයි.

`src/main/resources/application.properties` ගොනුව මඟින් Database එක තෝරාගත හැක:

### 1. MySQL (XAMPP) භාවිතයෙන් Run කිරීමට:
1. XAMPP Control Panel එකෙන් **Apache** සහ **MySQL** Start කරන්න.
2. `application.properties` ගොනුවේ ඇති MySQL settings වල comment ඉවත් කරන්න:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hotel_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   ```
   *(සටහන: `hotel_db` database එක MySQL තුළ auto-create වේ).*

### 2. H2 Embedded SQL Database (Default):
කිසිදු බාහිර මෘදුකාංගයක් run නොකර ක්ෂණිකව test කිරීමට default ලෙස H2 SQL database එක සකසා ඇත.

---

## 🚀 How to Run the Project

1. Terminal එකෙන් හෝ IDE (IntelliJ IDEA, Eclipse, VS Code) එකෙන් project එක open කර run කරන්න:
   ```bash
   mvn spring-boot:run
   ```
2. Web Browser එක විවෘත කර `http://localhost:8080` වෙත පිවිසෙන්න.

### 🔑 Demo Accounts (පරීක්ෂා කිරීම සඳහා):
- **Customer Account (Member 2 & 3):**
  - Email: `customer@hotel.com`
  - Password: `123456`
- **Hotel Owner Account (Member 1):**
  - Email: `owner@hotel.com`
  - Password: `123456`