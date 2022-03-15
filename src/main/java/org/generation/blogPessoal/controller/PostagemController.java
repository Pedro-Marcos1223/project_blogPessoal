package org.generation.blogPessoal.controller;

import java.util.List;

import javax.validation.Valid;

import org.generation.blogPessoal.model.Postagem;
import org.generation.blogPessoal.repository.PostagemRepository;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Controller postagem")
public class PostagemController {

	@Autowired
	private PostagemRepository repository;

	@Operation(summary = "Get all posts")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List whitch all posts", content = @Content),
			@ApiResponse(responseCode = "404", description = "There are no Posts", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping
	public ResponseEntity<List<Postagem>> GetAll() {
		return ResponseEntity.ok(repository.findAll());
	}

	@Operation(summary = "Get post by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Show the post found", content = {
					@Content(schema = @Schema(implementation = Postagem.class)) }),
			@ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping("/{id}")
	public ResponseEntity<Postagem> GetById(@PathVariable long id) {
		return repository.findById(id).map(resp -> ResponseEntity.ok(resp)).orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Get post by title")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Shows posts found with the same keywords", content = @Content),
			@ApiResponse(responseCode = "404", description = "There are no posts with this keyword", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> GetByTitulo(@PathVariable String titulo) {
		return ResponseEntity.ok(repository.findAllByTituloContainingIgnoreCase(titulo));
	}

	@Operation(summary = "Create a post")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Create a post", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Postagem.class)) }),
			@ApiResponse(responseCode = "400", description = "Error creating post", content = @Content),
			@ApiResponse(responseCode = "401", description = "Not authorized to create a post", content = @Content) })
	@PostMapping()
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {
		return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(postagem));
	}

	@Operation(summary = "Update a post")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Update the post", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Postagem.class)) }),
			@ApiResponse(responseCode = "400", description = "Error updating post", content = @Content),
			@ApiResponse(responseCode = "401", description = "Not authorized to update a post", content = @Content) })
	@PutMapping()
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
		return ResponseEntity.status(HttpStatus.OK).body(repository.save(postagem));
	}

	@Operation(summary = "Delete a post")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Delete the post", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized to delete post", content = @Content) })
	@DeleteMapping("/{id}")
	public void delete(@PathVariable long id) {
		repository.deleteById(id);
	}
}
