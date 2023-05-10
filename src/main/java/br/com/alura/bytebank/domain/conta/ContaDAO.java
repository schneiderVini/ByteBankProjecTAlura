package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;
import com.mysql.cj.protocol.Resultset;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private Connection conn;

    ContaDAO(Connection connection) {
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta) {
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente,true);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email,esta_ativa)" +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());
            preparedStatement.setBoolean(6,true);

            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Set<Conta> listar() {
        Set<Conta> contas = new HashSet<>();
        PreparedStatement ps;
        ResultSet rs;
        String sql =  "Select * from conta where esta_ativa = true";

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                contas.add(new Conta(rs.getInt(1),rs.getBigDecimal(2),
                        new Cliente(new DadosCadastroCliente(rs.getString(3), rs.getString(4),rs.getString(5))),rs.getBoolean(6)));
            }
            ps.close();
            rs.close();
            conn.close();

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }

    public Conta listarPorNumero(int numeroConta) {
        PreparedStatement ps;
        ResultSet rs;
        Conta conta = null;
        String sql =  "Select * from conta where numero = ? and esta_ativa = true";

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1,numeroConta);
            rs = ps.executeQuery();

            while (rs.next()) {
                conta = new Conta(rs.getInt(1),rs.getBigDecimal(2),
                        new Cliente(new DadosCadastroCliente(rs.getString(3), rs.getString(4),rs.getString(5))),rs.getBoolean(6));
            }
            ps.close();
            rs.close();
            conn.close();

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void alterar(Integer numeroDaConta, BigDecimal valor) {
        PreparedStatement ps;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1,valor);
            ps.setInt(2,numeroDaConta);
            ps.execute();

            ps.close();
            conn.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deletar(Integer numeroDaConta) {
        PreparedStatement ps;
        String sql = "DELETE FROM conta WHERE numero = ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1,numeroDaConta);
            ps.execute();

            ps.close();
            conn.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void alterarLogico(Integer numeroDaConta) {
        PreparedStatement ps;
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1,numeroDaConta);
            ps.execute();

            ps.close();
            conn.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
