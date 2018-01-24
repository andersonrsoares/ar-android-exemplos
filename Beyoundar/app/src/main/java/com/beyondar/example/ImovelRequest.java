package com.beyondar.example;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ander on 03/09/2016.
 */
public class ImovelRequest implements Serializable {

    List<Imovel> dados = new ArrayList<>();

    public List<Imovel> getDados() {
        return dados;
    }

    public void setDados(List<Imovel> dados) {
        this.dados = dados;
    }

    public void ImovelRequest(){

    }

    public ImovelRequest(JSONObject jsonObject, String finalidade) throws Exception {
        JSONArray jsonArray =  jsonObject.optJSONArray("dados");
        for(int i=0;i<jsonArray.length();i++){
           Imovel imovel = new Imovel();


            try {
                if(!"".equalsIgnoreCase(finalidade)){
                    imovel.setFinalidade(finalidade);
                }else{
                    imovel.setFinalidade(jsonArray.getJSONObject(i).getJSONArray("finalidades_imovel").getJSONObject(0).optString("slug"));
                }
            }catch (Exception ex){
                Log.e("", "ImovelRequest: ",ex );
            }

           imovel.setId(jsonArray.getJSONObject(i).optString("ID"));
            imovel.setCodigo(jsonArray.getJSONObject(i).optString("codigo"));
            imovel.setTitulo(jsonArray.getJSONObject(i).optString("titulo"));

            try {
                imovel.setTipo(jsonArray.getJSONObject(i).getJSONObject("tipo").optString("name"));
            }catch (Exception ex){

            }
            imovel.setBanheiros(jsonArray.getJSONObject(i).optString("imovel_banheiros"));
            imovel.setDormitorios(jsonArray.getJSONObject(i).optString("imovel_dormitorios"));
            imovel.setGaragens(jsonArray.getJSONObject(i).optString("imovel_garagens"));
            imovel.setLat(jsonArray.getJSONObject(i).optString("lat"));
            imovel.setLng(jsonArray.getJSONObject(i).optString("lng"));
            try {
                imovel.setRua(jsonArray.getJSONObject(i).optJSONObject("endereco").optString("rua"));
                imovel.setBairro(jsonArray.getJSONObject(i).optJSONObject("endereco").optString("bairro"));
                imovel.setCidade(jsonArray.getJSONObject(i).optJSONObject("endereco").optString("cidade"));
            }catch (Exception ex){

            }

            if(!"".equalsIgnoreCase(imovel.getFinalidade())){
                if("venda".equalsIgnoreCase(imovel.getFinalidade())){
                    imovel.setValor(jsonArray.getJSONObject(i).optString("valor_venda"));
                }else{
                    imovel.setValor(jsonArray.getJSONObject(i).optString("valor_aluguel"));
                }
            }else{
                try {
                    if(jsonArray.getJSONObject(i).getString("valor_venda").length() > 0)
                        imovel.setValor(jsonArray.getJSONObject(i).getString("valor_venda"));
                    else
                    if(jsonArray.getJSONObject(i).getString("valor_aluguel").length() > 0){
                        imovel.setValor(jsonArray.getJSONObject(i).getString("valor_aluguel"));
                    }
                }catch (Exception ex){

                }
            }


            imovel.setUrl(jsonArray.getJSONObject(i).optString("thumb_image_url"));
           // imovel.setFinalidade(finalidade);

            dados.add(imovel);
        }
    }

}
