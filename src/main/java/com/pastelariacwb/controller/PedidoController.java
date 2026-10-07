package com.pastelariacwb.controller;

import com.pastelariacwb.dto.PedidoRequest;
import com.pastelariacwb.model.Cliente;
import com.pastelariacwb.model.Pedido;
import com.pastelariacwb.model.Produto;
import com.pastelariacwb.repository.ClienteRepository;
import com.pastelariacwb.repository.PedidoRepository;
import com.pastelariacwb.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    // POST /pedidos - criar
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody PedidoRequest req) {
        return montarPedido(new Pedido(), req)
                .<ResponseEntity<?>>map(p -> ResponseEntity.status(HttpStatus.CREATED).body(pedidoRepository.save(p)))
                .orElseGet(() -> erro(req));
    }

    // GET /pedidos - listar todos
    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // GET /pedidos/{id} - consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /pedidos/{id} - atualizar (opcional)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody PedidoRequest req) {
        Optional<Pedido> existente = pedidoRepository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return montarPedido(existente.get(), req)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(pedidoRepository.save(p)))
                .orElseGet(() -> erro(req));
    }

    // DELETE /pedidos/{id} - apagar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- auxiliares ----------

    private Optional<Pedido> montarPedido(Pedido pedido, PedidoRequest req) {
        if (req.getClienteId() == null || req.getProdutoId() == null
                || req.getQuantidade() == null || req.getQuantidade() <= 0) {
            return Optional.empty();
        }
        Optional<Cliente> cliente = clienteRepository.findById(req.getClienteId());
        Optional<Produto> produto = produtoRepository.findById(req.getProdutoId());
        if (cliente.isEmpty() || produto.isEmpty()) {
            return Optional.empty();
        }
        pedido.setCliente(cliente.get());
        pedido.setProduto(produto.get());
        pedido.setQuantidade(req.getQuantidade());
        return Optional.of(pedido);
    }

    private ResponseEntity<?> erro(PedidoRequest req) {
        if (req.getQuantidade() == null || req.getQuantidade() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("erro", "quantidade deve ser maior que zero"));
        }
        if (req.getClienteId() == null || !clienteRepository.existsById(req.getClienteId())) {
            return ResponseEntity.badRequest().body(Map.of("erro", "cliente não encontrado"));
        }
        return ResponseEntity.badRequest().body(Map.of("erro", "produto não encontrado"));
    }
}
