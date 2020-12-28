
# Foodify 
Aplikasi native android sederhana menggunakan Java yang digunakan untuk menampilkan rekomendasi makanan sehat dan mencatat history makanan yang dimakan setiap hari. Dengan Firebase yang digunakan sebagai backend dari aplikasi.
  
## Screenshot Aplikasi  
<p align="center">   
  <img src="https://drive.google.com/uc?id=1Ule-I81tCZT0IVS-Oe4EayPRClBYHJnB" width="33%"/>  
  <img src="https://drive.google.com/uc?id=1VX28jGRfQoVQQQYWij_1r2kaa9aoIQ9V" width="33%"/>  
    <img src="https://drive.google.com/uc?id=1u5L6xVSifzTLDpVZ-l7gV_BZ4HSPIGyd" width="33%"/>  
      <img src="https://drive.google.com/uc?id=1reh2G9nLlpCy4XsPCoJKvZvZs5p5DYA2" width="33%"/>  
        <img src="https://drive.google.com/uc?id=1WK5MpRB46dXc-rYZpHHrNT5Dhf6t1N4l" width="33%"/>  
          <img src="https://drive.google.com/uc?id=1RHhGY0iUvW62bf5bkqsDJuoiKpMw0uXA" width="33%"/>  
</p>  

### Clone Instruction
<ol>
<li>Create firebase application.</li>
<li>Integrate with this app</li>
<li>Enable Firestore, Google Sign In, and Storage</li>
<li>Create database for food recommendation in Firestore formatted below</li>
</ol>
Collection Name: <b>food_recommendations</b> <br>
Field Specification

```json
{
	"title": string,
	"description": string,
	"imageUrl": string,
	"nutrientContent": []string
}
```
  
### Creator  
<a href="https://linkedin.com/in/christiandoxa/">  
Christian Doxa Hamasiah  
</a>