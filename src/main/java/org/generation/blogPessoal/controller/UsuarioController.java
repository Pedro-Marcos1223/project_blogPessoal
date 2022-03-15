package org.generation.blogPessoal.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.generation.blogPessoal.model.Postagem;
import org.generation.blogPessoal.model.UserLogin;
import org.generation.blogPessoal.model.Usuario;
import org.generation.blogPessoal.repository.UsuarioRepository;
import org.generation.blogPessoal.service.UsuarioService;
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
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

	@Autowired
	private UsuarioService service;

	@Autowired
	private UsuarioRepository repository;

	@Operation(summary = "Get all users")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List whitch all users", content = @Content),
			@ApiResponse(responseCode = "404", description = "There are no users", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping("/all")
	public ResponseEntity<List<Usuario>> getAll() {
		return ResponseEntity.ok(repository.findAll());
	}

	@Operation(summary = "Get users by name")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Show users with the same name", content = @Content),
			@ApiResponse(responseCode = "404", description = "There are no users with that name", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Usuario>> getByName(@PathVariable String nome) {

		if (repository.findAllByNomeContainingIgnoreCase(nome).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não há ninguem com esse nome...");
		}
		return ResponseEntity.ok(repository.findAllByNomeContainingIgnoreCase(nome));
	}

	@Operation(summary = "Get user by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Show the user found", content = {
					@Content(schema = @Schema(implementation = Usuario.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> getById(@PathVariable long id) {
		return repository.findById(id).map(resp -> ResponseEntity.ok(resp)).orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "login the user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "login success", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
			@ApiResponse(responseCode = "400", description = "incorrect user information", content = @Content) })
	@PostMapping("/logar")
	public ResponseEntity<UserLogin> autenticationUsuario(@RequestBody Optional<UserLogin> usuario) {
		return service.logarUsuario(usuario).map(resp -> ResponseEntity.ok(resp))
				.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}

	@Operation(summary = "Create a User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Create a user", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
			@ApiResponse(responseCode = "400", description = "Error creating user", content = @Content) })
	@PostMapping("/cadastrar")
	public ResponseEntity<Usuario> postUsuario(@Valid @RequestBody Usuario usuario) {
		return service.cadastrarUsuario(usuario).map(resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp))
				.orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
	}

	@Operation(summary = "Update a user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Update the user", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
			@ApiResponse(responseCode = "400", description = "Error updating user", content = @Content),
			@ApiResponse(responseCode = "401", description = "Not authorized to update a user", content = @Content) })
	@PutMapping("/atualizar")
	public ResponseEntity<Usuario> putUsuario(@Valid @RequestBody Usuario usuario) {
		return service.atualizarUsuario(usuario).map(resp -> ResponseEntity.status(HttpStatus.OK).body(resp))
				.orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
	}

	@Operation(summary = "Delete a user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Delete the user", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized to delete user", content = @Content) })
	@DeleteMapping("/{id}")
	public void deleteUsuario(@PathVariable Long id) {
		repository.deleteById(id);
	}

}