# README

## Deployment

## Tips

### JANGAN LUPA GANTI BRANCH
Untuk setiap fitur, bisa buat branch baru. biar branch `main` gak perlu diutak atik dan deployment tetap berjalan dengan aman.

Setelah selesai pengerjaan fitur, gunakan fitur Pull Request pada github untuk melakukan merge branch fitur ke branch `development`.

### CARA PEMAKAIAN RESPONSE UTIL

Gunakan `ResponseUtil.build` untuk membuat Response Entity, biasa digunakan untuk menggantikan perintah `ResponseEntity.ok().body("data");`

Contoh pemakaian :

![Service](https://github.com/Fel-1/Memo/blob/master/Picture/Response%20Util%20Tutorial/ServiceResponseUtil.jpg)

Parameter `AppConstant.ResponseCode.SUCCESS` untuk mengirim response terhadap request, tipe tipe response ada di file [AppConstant](./src/main/java/com/capstone/booking/constant/AppConstant.java), jika response yang dibutuhkan tidak ada, bisa langsung dibuat di file AppConstantnya.

Contoh Response : 

![ResponseCode](https://github.com/Fel-1/Memo/blob/master/Picture/Response%20Util%20Tutorial/ResponseCode.jpg)

Kalau masih bingung bisa langsung ditanyakan aja.