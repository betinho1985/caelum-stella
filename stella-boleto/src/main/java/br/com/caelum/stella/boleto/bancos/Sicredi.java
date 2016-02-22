package br.com.caelum.stella.boleto.bancos;

import br.com.caelum.stella.boleto.Banco;
import br.com.caelum.stella.boleto.Beneficiario;
import br.com.caelum.stella.boleto.Boleto;
import static br.com.caelum.stella.boleto.utils.StellaStringUtils.leftPadWithZeros;
import static java.lang.String.format;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Paulo
 * @since 19/02/2016
 * @version 1.0.0
 *
 */
public class Sicredi extends AbstractBanco implements Banco {

    private static final long serialVersionUID = 1L;

    private static final String NUMERO_SICREDI = "748";
    private static final String DIGITO_SICREDI = "X";

    @Override
    public String getNumeroFormatado() {
        return NUMERO_SICREDI;
    }

    @Override
    public URL getImage() {
        String arquivo = "/br/com/caelum/stella/boleto/img/%s.png";
        String imagem = format(arquivo, getNumeroFormatado());
        return getClass().getResource(imagem);
    }

    @Override
    public String geraCodigoDeBarrasPara(Boleto boleto) {
        Beneficiario beneficiario = boleto.getBeneficiario();
        StringBuilder campoLivre = new StringBuilder();

        campoLivre.append("3");//codigo numerico referente ao tipo de cobranca ("1" com registro | "3" sem registro
        campoLivre.append(beneficiario.getCarteira()); //carteira
        campoLivre.append(getNossoNumeroFormatado(beneficiario)); //nosso numero
        campoLivre.append(beneficiario.getAgencia()); // agencia do beneficiario
        campoLivre.append(beneficiario.getPosto()); // posto (peculiaridade do sicredi)
        campoLivre.append(beneficiario.getCodigoBeneficiario()); // codigo do beneficiario
        campoLivre.append("1"); //Será 1 (um) quando houver valor expresso no campo "valor do documento"
        campoLivre.append("0"); // filler
        campoLivre.append(geradorDeDigito.geraDigitoMod11AceitandoRestoZero(campoLivre.toString())); //digito verificador

        return new CodigoDeBarrasBuilder(boleto).comCampoLivre(campoLivre);
    }

    @Override
    public String getCodigoBeneficiarioFormatado(Beneficiario beneficiario) {
        return leftPadWithZeros(beneficiario.getCodigoBeneficiario(), 5);
    }

    @Override
    public String getCarteiraFormatado(Beneficiario beneficiario) {
        return leftPadWithZeros(beneficiario.getCarteira(), 2);
    }

    @Override
    public String getNossoNumeroFormatado(Beneficiario beneficiario) {
        return leftPadWithZeros(beneficiario.getNossoNumero().concat(beneficiario.getDigitoNossoNumero()), 9);
    }

    @Override
    public String getNumeroFormatadoComDigito() {
        StringBuilder builder = new StringBuilder();
        builder.append(getNumeroFormatado()).append("-");
        return builder.append(DIGITO_SICREDI).toString();
    }

    @Override
    public String getNossoNumeroECodigoDocumento(Boleto boleto) {

        Beneficiario beneficiario = boleto.getBeneficiario();

        String nossoNumero = getNossoNumeroFormatado(beneficiario);
        StringBuilder sb = new StringBuilder(nossoNumero);
        sb.insert(2, "/");
        sb.insert(9, "-");

        return sb.toString();
    }

}
