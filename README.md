# Mini İşe Alım Takip Sistemi (ATS)

İş ilanları ile adayları eşleştirip her başvuruyu **başvurudan işe alıma** kadar aşama aşama takip eden bir işe alım yönetim sistemi.

**Canlı API:** https://ats-api-btur.onrender.com
> Ücretsiz sunucu kullanıldığı için ilk istek 50 saniye kadar sürebilir (uykudan uyanma süresi).

---

## Ne işe yarar

Açık pozisyonlar tanımlanır, adaylar kaydedilir, adaylar ilanlara başvurtulur. Her başvuru bir adayın işe alım yolculuğunu temsil eder; bu yolculuk boyunca yapılan görüşmeler, alınan notlar ve verilen puanlar tek yerde toplanır.

*"Şu ilana kaç kişi başvurdu, kaçı mülakatta, kime teklif verdik?"* sorusunun cevabını her an gösterir.

---

## Teknolojiler

| Katman | Teknoloji |
|---|---|
| Dil / Çatı | Java 21, Spring Boot 4 |
| Veri erişimi | Spring Data JPA, Hibernate |
| Veritabanı | H2 (geliştirme), PostgreSQL (canlı) |
| Doğrulama | Jakarta Bean Validation |
| PDF işleme | Apache PDFBox |
| Yapay zeka | Anthropic Claude API (`claude-haiku-4-5`) |
| Yayınlama | Docker, Render |

---

## Mimari

Katmanlı mimari kullanıldı; her katmanın tek bir sorumluluğu var:

```
controller  →  HTTP istekleri, JSON dönüşümü
service     →  İŞ KURALLARI (aşama akışı, tekrar engelleme, uyum skoru)
repository  →  Veritabanı erişimi
model       →  Veri yapıları (entity'ler)
dto         →  Dışarıya açılan veri şekilleri
exception   →  Hata türleri ve merkezî hata yönetimi
```

**Tasarım ilkesi:** Bütün iş kuralları service katmanında. Controller HTTP'den başka bir şey bilmiyor, repository de sadece veriye erişiyor. Böylece bir kural HTTP'ye bağlı kalmıyor — başvuru oluşturma ileride farklı bir yerden tetiklense de aynı kurallar işler.

### Veri modeli

```
Aday  1 ──< Basvuru >── 1  Ilan
              │
              └──< Aktivite
```

Bir aday birçok ilana, bir ilana birçok aday başvurabilir — çoktan-çoğa bir ilişki. Bu ilişki doğrudan kurulmak yerine araya **Başvuru** varlığı konuldu; böylece iki adet çoktan-bire ilişkiye dönüştü.

Araya varlık konulmasının sebebi sadece bağlantı değil: **başvurunun kendi verisi var** — hangi aşamada olduğu ve başvuru tarihi. Bu bilgi ne adaya ne ilana aittir. Aynı aday bir ilanda mülakat aşamasındayken başka bir ilanda elenmiş olabilir.

---

## Öne çıkan özellikler

### Aşama akışı (durum makinesi)

```
BASVURU → ON_ELEME → MULAKAT → TEKLIF → ISE_ALINDI
    └──────────── (her aşamadan) ────────────▶ ELENDI
```

- Aşama **atlanamaz**; her geçiş bir öncekine bağlı.
- `ISE_ALINDI` ve `ELENDI` bitiş durumlarıdır — oradan ilerlenemez.
- Eleme sıralı akışın dışındadır, herhangi bir aşamadan yapılabilir (işe alınmış aday hariç).

Geçiş kuralları tek yerde tanımlı olduğu için araya yeni bir aşama eklemek kolaydır.

### Tekrar başvuru kuralı

Aynı adayın aynı ilana ikinci kez eklenmesi koşulsuz engellenmez; engel yalnızca **adayın o ilanda devam eden bir süreci varsa** uygulanır.

| Adayın o ilandaki son aşaması | Yeni başvuru |
|---|---|
| `BASVURU`, `ON_ELEME`, `MULAKAT`, `TEKLIF` | ❌ Engellenir (409) |
| `ELENDI` | ✅ İzin verilir — aday yeniden değerlendirilebilir |
| `ISE_ALINDI` | ✅ İzin verilir — ayrılıp geri dönebilir |

**Neden:** Elenmiş bir aday, sonraki dönemde deneyim kazanmış ya da pozisyonun gereksinimleri değişmiş olabilir; işe alınmış biri de ayrılıp aynı pozisyona tekrar başvurabilir. Asıl önlenmek istenen, **aynı anda iki açık sürecin** yürümesidir — süreci sonlanmış başvuru geçmişte kalır, yenisi ayrı bir kayıt olarak açılır.

Aktif sayılan aşamalar service katmanında tek bir listede tanımlıdır; kural değişirse tek yer güncellenir.

### Uyum skoru

Adayın yetenekleri ile ilanın aradığı nitelikler karşılaştırılıp yüzde uyum hesaplanır.

```
İlan  : "Java, Spring, SQL"
Aday  : "Java, SQL, Git"
        Java ✓  Spring ✗  SQL ✓   →  2/3 = %66
```

Payda **ilanın nitelikleri**dir: sorulan soru "adayın bildiklerinin ne kadarı işe yarıyor" değil, **"işin gerektirdiklerinin ne kadarını karşılıyor"**. Böylece adayın fazladan yetenekleri skoru düşürmez.

Sadece yüzde değil, **eşleşen ve eksik nitelik listeleri** de döner — iki aday aynı skoru farklı sebeplerle alabilir:

| Aday | Yetenekler | Skor | Eşleşen | Eksik |
|---|---|---|---|---|
| Ahmet | Java, SQL, Git | 66 | java, sql | spring |
| Ayşe | Java, Spring | 66 | java, spring | sql |

### Özgeçmiş okuma (AI)

PDF özgeçmiş yüklenir; sistem metni çıkarıp bir dil modeline gönderir ve ad-soyad, e-posta, telefon, yetenekler ile kısa özeti yapılandırılmış biçimde alır.

**Neden dil modeli:** Özgeçmişlerin standart bir biçimi yok — kimi belgede yetenekler madde madde, kimisinde paragraf içinde, farklı başlıklar altında. Sabit kurallarla bu çeşitlilik yakalanamaz; ayrıca özet üretmek metni anlamayı gerektirir.

**Önizleme + onay akışı:** Çıkan bilgiler doğrudan kaydedilmez, önce kullanıcıya döner. Model hata yapabilir; kullanıcı kontrol edip düzelttikten sonra mevcut aday oluşturma uç noktasına gönderir. Yapay zeka burada karar verici değil, **veri girişini hızlandıran bir yardımcıdır**.

### Raporlama

- **İlan bazlı aşama dağılımı:** bir ilana başvuranlar aşamalara göre gruplanır, her aşamadaki sayı gösterilir.
- **Huni (funnel) raporu:** tüm sistemdeki dönüşüm; başvurudan işe alıma her aşamada kaç aday kaldığı.

### Hata yönetimi

Hatalar anlamlarına göre sınıflandırılır ve doğru HTTP kodlarıyla döner:

| Durum | Kod | Örnek |
|---|---|---|
| Kayıt bulunamadı | **404** | "Aday bulunamadı: 999" |
| Mevcut durumla çelişki | **409** | "Bu adayin bu ilanda devam eden bir basvurusu var" |
| Geçersiz veri | **400** | "Puan 1 ile 5 arasında olmalıdır" |

Çeviri işini merkezî bir hata karşılayıcı yapar; service katmanı HTTP'den haberdar değildir.

---

## API uç noktaları

> `/` ve `/api/auth/login` dışındaki tüm uç noktalar geçerli bir token gerektirir
> 
### Kimlik doğrulama
| Fiil | Adres | Açıklama |
|---|---|---|
| POST | `/api/auth/login` | Giriş yap, token al |
| GET | `/` | Karşılama ve endpoint listesi (token gerektirmez) |

### İlanlar
| Fiil | Adres | Açıklama |
|---|---|---|
| POST | `/api/ilanlar` | İlan oluştur |
| GET | `/api/ilanlar` | İlanları listele |
| GET | `/api/ilanlar/{id}` | Tek ilan |
| PUT | `/api/ilanlar/{id}` | İlan güncelle (açık/kapalı yönetimi) |
| DELETE | `/api/ilanlar/{id}` | İlan sil |

### Adaylar
| Fiil | Adres | Açıklama |
|---|---|---|
| POST | `/api/adaylar` | Aday oluştur |
| GET | `/api/adaylar` | Adayları listele |
| GET | `/api/adaylar/{id}` | Tek aday |
| PUT | `/api/adaylar/{id}` | Aday güncelle |
| DELETE | `/api/adaylar/{id}` | Aday sil |
| POST | `/api/adaylar/cv-parse` | PDF özgeçmişten bilgi çıkar (kayıt oluşturmaz) |

### Başvurular
| Fiil | Adres | Açıklama |
|---|---|---|
| POST | `/api/basvurular?adayId=&ilanId=` | Başvuru oluştur |
| GET | `/api/basvurular` | Başvuruları listele |
| GET | `/api/basvurular/{id}` | Başvuru detayı (aktivite geçmişiyle) |
| POST | `/api/basvurular/{id}/ilerlet` | Bir sonraki aşamaya geçir |
| POST | `/api/basvurular/{id}/ele` | Başvuruyu ele |
| GET | `/api/basvurular/{id}/uyum` | Uyum skoru |
| GET | `/api/basvurular/ilan/{ilanId}` | Bir ilanın başvuruları |
| GET | `/api/basvurular/ilan/{ilanId}/asama-raporu` | İlan bazlı aşama dağılımı |
| GET | `/api/basvurular/rapor/funnel` | Genel huni raporu |
| GET | `/api/basvurular/ise-alinanlar?departman=` | İşe alınanlar; `departman` opsiyonel, verilmezse tümü |

### Aktiviteler
| Fiil | Adres | Açıklama |
|---|---|---|
| POST | `/api/basvurular/{basvuruId}/aktiviteler?tip=&icerik=&puan=` | Aktivite ekle |
| GET | `/api/basvurular/{basvuruId}/aktiviteler` | Aktiviteleri tarih sırasıyla listele |

`tip` değerleri: `NOT`, `GORUSME`, `DEGERLENDIRME` (puan yalnızca `DEGERLENDIRME` için, 1–5 arası).

---

## Demo erişimi

Tanıtım amaçlı iki hesap tanımlıdır:

| Rol | E-posta | Şifre |
|---|---|---|
| Yönetici | admin@ats.com | demo1234 |
| İK Uzmanı | ik@ats.com | demo1234 |

Giriş:

```bash
curl -sS -X POST https://ats-api-btur.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ats.com","sifre":"demo1234"}'
```

Dönen token sonraki isteklerde `Authorization: Bearer <token>` başlığıyla gönderilir.

> Bu hesaplar yalnızca demo içindir. Gerçek kullanımda kaldırılmalı, kullanıcılar yönetici tarafından tanımlanmalıdır.

---

### Kimlik doğrulama (JWT)

Kullanıcı e-posta ve şifresiyle giriş yapar, sistem imzalı bir token döndürür.
Sonraki her istekte bu token `Authorization: Bearer <token>` başlığıyla gönderilir;
sunucu imzayı doğrulayıp kimliği tanır.

**Durumsuz (stateless) tasarım:** Sunucu hiçbir oturum bilgisi tutmaz. Kimlik her
istekte token'dan gelir — ölçeklenmede ve farklı istemci türlerinde avantaj sağlar.

**Şifre saklama:** Şifreler BCrypt ile hash'lenir; veritabanında düz metin tutulmaz.
Doğrulama, girilen şifrenin aynı tuzla hash'lenip karşılaştırılmasıyla yapılır.

**Roller ve yetkiler:**

| İşlem | ADMIN | IK_UZMANI |
|---|---|---|
| Görüntüleme | ✓ | ✓ |
| Oluşturma, güncelleme | ✓ | ✓ |
| Silme | ✓ | ✗ |

> Rol ayrımı şu an silme işlemiyle sınırlıdır. İlan oluşturma, aşama ilerletme gibi
> işlemler de aynı yapıyla ayrıştırılabilir.

**Hata cevapları:** Token yoksa veya geçersizse `401`, yetki yetmiyorsa `403` —
ikisi de anlamlı JSON mesajıyla döner.

Token süresi 24 saattir. Gizli anahtar `JWT_SECRET` ortam değişkeninden okunur.

## Yerelde çalıştırma

**Gereksinimler:** Java 21

```bash
git clone https://github.com/Bertanokt/ats.git
cd ats
```

Özgeçmiş okuma özelliği için Anthropic API anahtarı gerekir:

```bash
export ANTHROPIC_API_KEY=sk-ant-...
```

Çalıştır:

```bash
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde açılır. Varsayılan profil `dev`'dir; H2 veritabanı `data/` klasöründe dosya olarak tutulur. Açılışta örnek veri (3 ilan, 5 aday, 6 başvuru, 5 aktivite) otomatik oluşturulur.

### Hızlı deneme

```bash
curl -sS http://localhost:8080/api/ilanlar | jq
curl -sS http://localhost:8080/api/basvurular/rapor/funnel | jq
curl -sS http://localhost:8080/api/basvurular/1/uyum | jq
```

---

## Yapılandırma

Ortak ayarlar `application.properties`'te; veritabanı bağlantısı profillere ayrılmıştır:

- `application-dev.properties` → H2 (dosya tabanlı)
- `application-prod.properties` → PostgreSQL

Profil seçimi `SPRING_PROFILES_ACTIVE` ortam değişkeniyle yapılır, tanımlı değilse `dev` kullanılır. Veri erişim katmanı sayesinde veritabanı değişimi kaynak kodda değişiklik gerektirmez.

**Ortam değişkenleri:**

| Değişken | Açıklama |
|---|---|
| `ANTHROPIC_API_KEY` | Özgeçmiş okuma için API anahtarı |
| `SPRING_PROFILES_ACTIVE` | `dev` veya `prod` |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC adresi (yalnızca `prod`) |
| `SPRING_DATASOURCE_USERNAME` | Veritabanı kullanıcısı (yalnızca `prod`) |
| `SPRING_DATASOURCE_PASSWORD` | Veritabanı şifresi (yalnızca `prod`) |
| `JWT_SECRET` | Token imzalama anahtarı (en az 32 karakter) |

Gizli bilgiler kaynak koda yazılmaz, ortam değişkeniyle sağlanır.

---

## Bilinen eksikler / yol haritası

- [ ] React (Vite) ile kullanıcı arayüzü — kanban panosu, formlar, huni grafiği
- [ ] Birim ve entegrasyon testleri
- [ ] Sayfalama, arama ve filtreleme
- [ ] Aşama değişiminde otomatik görev/hatırlatma üretimi
- [ ] Aday zaman çizelgesi (tüm ilanlardaki geçmiş tek akışta)

---