package org.generation.blogPessoal.controller;

import java.util.List;

import javax.validation.Valid;

import org.generation.blogPessoal.model.Tema;
import org.generation.blogPessoal.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/tema")
public class TemaController {

	@Autowired
	private TemaRepository repository;

	@Operation(summary = "Get all topic")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List whitch all topics", content = @Content),
			@ApiResponse(responseCode = "404", description = "There are no topics", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping
	public ResponseEntity<List<Tema>> getAll() {
		return ResponseEntity.ok(repository.findAll());
	}

	@Operation(summary = "Get topics by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Show the topcs found", content = {
					@Content(schema = @Schema(implementation = Tema.class)) }),
			@ApiResponse(responseCode = "404", description = "Topics not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping("/{id}")
	public ResponseEntity<Tema> getById(@PathVariable Long id) {
		return repository.findById(id).map(resp -> ResponseEntity.ok(resp)).orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Get topics by description")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Shows topics found with the same keywords", content = @Content),
			@ApiResponse(responseCode = "404", description = "There are no topics with this keyword", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping("/descricao/{descricao}")
	public ResponseEntity<List<Tema>> getByTitle(@PathVariable String descricao) {
		if (repository.findAllByDescricaoContainingIgnoreCase(descricao).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não foi encontrado temas com essa descrição.");
		}
		return ResponseEntity.ok(repository.findAllByDescricaoContainingIgnoreCase(descricao));
	}

	@Operation(summary = "Create a topic")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Create a topic", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Tema.class)) }),
			@ApiResponse(responseCode = "400", description = "Error creating topic", content = @Content),
			@ApiResponse(responseCode = "401", description = "Not authorized to create a topic", content = @Content) })
	@PostMapping
	public ResponseEntity<Tema> post(@Valid @RequestBody Tema tema) {
		return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(tema));
	}

	@Operation(summary = "Update a topic")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Update the topic", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Tema.class)) }),
			@ApiResponse(responseCode = "400", description = "Error updating topic", content = @Content),
			@ApiResponse(responseCode = "401", description = "Not authorized to update a topic", content = @Content) })
	@PutMapping
	public ResponseEntity<Tema> put(@Valid @RequestBody Tema tema) {
		return repository.findById(tema.getId()).map(resp -> ResponseEntity.ok(repository.save(tema)))
				.orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
	}

	@Operation(summary = "Delete a topic")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Delete the topic", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized to delete topic", content = @Content) })
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		repository.deleteById(id);
	}

}
