
package com.beyondar.example;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

;


public class Imovel implements Serializable {
    private String id;
    private String codigo;
    private String titulo;
    private String lat;
    private String Lng;
    private String tipo;
    private String habilitacao;
    private String finalidade;

    private String rua;
    private String cidade;
    private String bairro;
    private String url;


    private String status;
    private String dormitorios;
    private String banheiros;
    private String garagens;
    private String valor;
    private float distancia;

    public Imovel(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }


    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        if(cidade==null)
            return"";
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        if(bairro==null)
            return"";
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getUrl() {
        if(url==null)
            return "";
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDormitorios() {
        if(dormitorios==null||"0".equalsIgnoreCase(dormitorios))
            return "";
        return dormitorios;
    }

    public void setDormitorios(String dormitorios) {
        this.dormitorios = dormitorios;
    }

    public String getBanheiros() {
        return banheiros;
    }

    public void setBanheiros(String banheiros) {
        this.banheiros = banheiros;
    }

    public String getGaragens() {
        if(garagens==null||"0".equalsIgnoreCase(garagens))
            return "";
        return garagens;
    }

    public void setGaragens(String garagens) {
        this.garagens = garagens;
    }

    public String getValor() {
        if(valor==null)
            return "";
        return valor;
    }

    public String getValorFormat() {
        try {
            float vf = Float.parseFloat(valor.replace(".","").replace(",00",".00"));
            String sv = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(vf);
            if("venda".equalsIgnoreCase(finalidade)){
                return sv.replace(",00","");
            }else{
                return sv.replace(",00","") ;
            }
        }catch (Exception ex) {
              return "R$"+valor;
        }
    }

    public String getValorFormatSemTaxa() {
        try {
            //float vf = Float.parseFloat(valor);
            //return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(vf);
            return "R$"+valor;
        }catch (Exception ex) {

        }
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getHabilitacao() {
        return habilitacao;
    }

    public void setHabilitacao(String habilitacao) {
        this.habilitacao = habilitacao;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public String getFinalidadeFormat() {
        if("venda".equalsIgnoreCase(finalidade)){
            return  "Venda";
        }else{
            return "Locação";
        }
    }



    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        if(titulo==null)
            return "";
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }
}
