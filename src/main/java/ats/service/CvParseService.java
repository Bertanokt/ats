package ats.service;

import ats.dto.CvParseDto;
import ats.exception.GecersizIstekException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CvParseService {

    private static final long MAX_BOYUT = 5 * 1024 * 1024;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.model}")
    private String model;

    // --- Ana metot: PDF -> CvParseDto ---
    public CvParseDto parseEt(MultipartFile dosya) {
        String metin = pdfMetniCikar(dosya);
        String jsonCevap = llmeSor(metin);
        return jsonaCevir(jsonCevap);
    }

    /**
     * Veritabaninda kayitli CV icin. Dosya yuklenirken zaten dogrulandigi icin
     * tip/boyut kontrolleri tekrarlanmaz; metin cikarma, LLM istegi ve JSON
     * cevirisi MultipartFile akisiyla ayni kodu kullanir.
     */
    public CvParseDto parseEt(byte[] icerik) {
        String metin = pdfMetniCikar(icerik);
        String jsonCevap = llmeSor(metin);
        return jsonaCevir(jsonCevap);
    }

    // --- 1. LLM'e sor ---
    private String llmeSor(String cvMetni) {
        String prompt = """
                Asagida bir CV'nin duz metni var. Bu metinden su bilgileri cikar ve
                SADECE JSON olarak dondur. Aciklama, markdown veya kod bloğu ekleme.

                JSON formati:
                {"adSoyad": "", "email": "", "telefon": "", "yetenekler": "", "ozet": ""}

                Kurallar:
                - yetenekler: teknik beceriler, virgulle ayrilmis tek satir (ornek: "Java, Spring, SQL")
                - ozet: adayin deneyimini ozetleyen tek cumle (ornek: "3 yil backend deneyimi")
                - Bir bilgi bulunamazsa o alani bos string birak
                - Telefonu sadece rakam olarak yaz

                CV metni:
                ---
                %s
                ---
                """.formatted(cvMetni);

        Map<String, Object> istek = Map.of(
                "model", model,
                "max_tokens", 1000,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            Map<String, Object> cevap = restClient.post()
                    .uri(apiUrl)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .body(istek)
                    .retrieve()
                    .body(Map.class);

            // Cevap yapisi: {"content": [{"type": "text", "text": "..."}]}
            List<Map<String, Object>> content = (List<Map<String, Object>>) cevap.get("content");
            return (String) content.get(0).get("text");

        } catch (Exception e) {
            throw new GecersizIstekException("CV analiz edilemedi: " + e.getMessage());
        }
    }

    // --- 2. JSON metnini DTO'ya cevir ---
    private CvParseDto jsonaCevir(String json) {
        try {
            // LLM bazen ```json ... ``` sarmalayabilir, temizle
            String temiz = json.trim()
                    .replaceAll("^```json", "")
                    .replaceAll("^```", "")
                    .replaceAll("```$", "")
                    .trim();

            return objectMapper.readValue(temiz, CvParseDto.class);

        } catch (Exception e) {
            throw new GecersizIstekException("CV verisi okunamadi: " + e.getMessage());
        }
    }

    public String pdfMetniCikar(MultipartFile dosya){

        //1. dosya boş mu?
        if (dosya == null || dosya.isEmpty()){
            throw new GecersizIstekException("Dosya bos olamaz");
        }
        //2. PDF mi?
        String tip = dosya.getContentType();
        if (tip == null || !tip.equals("application/pdf")){
            throw new GecersizIstekException("Sadece PDF dosyasi yüklenebilir");
        }
        //3. Boyut kontrolü
        if (dosya.getSize() > MAX_BOYUT){
            throw new GecersizIstekException("Dosya boyutu 5MB yi aşamaz");
        }
        //4. Metni çıkar
        try {
            return pdfMetniCikar(dosya.getBytes());
        } catch (IOException e){
            throw new GecersizIstekException("PDF okunamadi: " + e.getMessage());
        }
    }

    // Ham PDF baytlarindan metin cikarir. Iki akis da buraya duser.
    public String pdfMetniCikar(byte[] icerik){
        if (icerik == null || icerik.length == 0){
            throw new GecersizIstekException("Dosya bos olamaz");
        }

        try(PDDocument belge = Loader.loadPDF(icerik)){
            PDFTextStripper stripper = new PDFTextStripper();
            String metin = stripper.getText(belge);

            if (metin == null || metin.isBlank()) {
                throw new GecersizIstekException("PDF'ten metin okunamadi (taranmis gorsel olabilir)");
            }

            return metin;

        }
        catch (IOException e){
            throw new GecersizIstekException("PDF okunamadi: " + e.getMessage());
        }
    }
}
