package br.com.dbc.vemser.financeiro.repository.oldRepositories;


import org.springframework.stereotype.Repository;

@Repository
public class ContatoRepository2 {

//    @Override
//    public Integer getProximoId(Connection connection) throws BancoDeDadosException {
//        try {
//            String sql = "SELECT SEQ_CONTATO.NEXTVAL mysequence from DUAL";
//            Statement stmt = connection.createStatement();
//            ResultSet res = stmt.executeQuery(sql);
//
//            if (res.next()) {
//                return res.getInt("mysequence");
//            }
//
//            return null;
//        } catch (SQLException e) {
//            throw new BancoDeDadosException(e.getCause());
//        }
//    }
//
//    @Override
//    public Contato adicionar(Contato contato) throws BancoDeDadosException {
//        Connection con = null;
//        try {
//            con = ConexaoBancoDeDados.getConnection();
//
//            Integer proximoId = this.getProximoId(con);
//            contato.setIdContato(proximoId);
//
//            String sql = """
//                    INSERT INTO contato
//                    (id_contato, id_cliente, telefone, email)
//                    VALUES(?,?,?,?)
//                    """;
//
//            PreparedStatement stmt = con.prepareStatement(sql);
//
//            stmt.setInt(1, contato.getIdContato());
//            stmt.setInt(2, contato.getIdCliente());
//            stmt.setString(3, contato.getTelefone());
//            stmt.setString(4, contato.getEmail());
//
//            // Executar consulta
//            stmt.executeUpdate();
//            return contato;
//
//        } catch (SQLException e) {
//            throw new BancoDeDadosException(e.getCause());
//        } finally {
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public boolean remover(Integer id) throws BancoDeDadosException {
//        Connection con = null;
//        try {
//            con = ConexaoBancoDeDados.getConnection();
//
//            String sql = "DELETE FROM contato WHERE id_contato = ?";
//
//            PreparedStatement stmt = con.prepareStatement(sql);
//
//            stmt.setInt(1, id);
//
//            // Executar consulta
//            int res = stmt.executeUpdate();
//            return res > 0;
//
//        } catch (SQLException e) {
//            throw new BancoDeDadosException(e.getCause());
//        } finally {
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public Contato editar(Integer id, Contato contato) throws BancoDeDadosException {
//        Connection con = null;
//        try {
//            con = ConexaoBancoDeDados.getConnection();
//
//            StringBuilder sql = new StringBuilder();
//            sql.append("UPDATE contato SET \n");
//
//            if(contato.getTelefone() != null){
//                sql.append(" telefone = ?,");
//            }
//
//            if(contato.getEmail() != null){
//                sql.append(" email = ?,");
//            }
//
//            sql.deleteCharAt(sql.length() - 1); //remove o ultimo ','
//            sql.append(" WHERE ID_CONTATO = ? ");
//
//            PreparedStatement stmt = con.prepareStatement(sql.toString());
//
//            int index = 1;
//            if (contato.getTelefone() != null) {
//                stmt.setString(index++, contato.getTelefone());
//            }
//
//            if (contato.getEmail() != null) {
//                stmt.setString(index++, contato.getEmail());
//            }
//
//            stmt.setInt(index, id);
//
//            // Executar consulta
//            stmt.executeUpdate();
//
//            return retornarContato(id);
//        } catch (SQLException e) {
//            throw new BancoDeDadosException(e.getCause());
//        } finally {
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public List<Contato> listar() throws BancoDeDadosException {
//        List<Contato> contatos = new ArrayList<>();
//        Connection con = null;
//        try {
//            con = ConexaoBancoDeDados.getConnection();
//            Statement stmt = con.createStatement();
//
//            String sql = """
//                        SELECT *  FROM contato c
//                        INNER JOIN cliente c2 ON c.id_cliente = c2.id_cliente
//                    """;
//
//            // Executa-se a consulta
//            ResultSet res = stmt.executeQuery(sql);
//
//            while (res.next()) {
//                Contato contato = getContatoFromResultSet(res);
//                contatos.add(contato);
//            }
//            return contatos;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new BancoDeDadosException(e.getCause());
//        } finally {
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public List<Contato> listarContatosPorPessoa(Integer idCliente) throws BancoDeDadosException {
//        List<Contato> contatos = new ArrayList<>();
//        Connection con = null;
//        try {
//            con = ConexaoBancoDeDados.getConnection();
//
//            String sql = """
//                        SELECT * FROM contato c
//                        WHERE c.id_cliente = ?
//                    """;
//
//            // Executa-se a consulta
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setInt(1, idCliente);
//
//            ResultSet res = stmt.executeQuery();
//
//            while (res.next()) {
//                Contato contato = getContatoFromResultSet(res);
//                contatos.add(contato);
//            }
//            return contatos;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new BancoDeDadosException(e.getCause());
//        } finally {
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public Contato retornarContato(Integer idContato) throws BancoDeDadosException {
//        Connection con = null;
//        try {
//            con = ConexaoBancoDeDados.getConnection();
//            Contato contato = new Contato();
//
//            String sql = """
//                        SELECT c.* FROM CONTATO c
//                        WHERE c.id_contato = ?
//                    """;
//
//            // Executa-se a consulta
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setInt(1, idContato);
//
//            ResultSet res = stmt.executeQuery();
//
//            while (res.next()){
//                contato = getContatoFromResultSet(res);
//            }
//            return contato;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new BancoDeDadosException(e.getCause());
//        } finally {
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private Contato getContatoFromResultSet(ResultSet res) throws SQLException {
//        Contato contato = new Contato();
//        contato.setIdContato(res.getInt("id_contato"));
//        contato.setIdCliente(res.getInt("id_cliente"));
//        contato.setTelefone(res.getString("telefone"));
//        contato.setEmail(res.getString("email"));
//        return contato;
//    }
}
