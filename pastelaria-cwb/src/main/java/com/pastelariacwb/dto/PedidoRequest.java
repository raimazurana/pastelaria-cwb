package com.pastelariacwb.dto;

/**
 * Corpo JSON recebido no POST/PUT de /pedidos.
 * Segue o DER: clienteId, produtoId e quantidade.
 */
public class PedidoRequest {

    private Long clienteId;
    private Long produtoId;
    private Integer quantidade;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
