package com.arquitecturajava;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.arquitecturajava.model.Cliente;
import com.arquitecturajava.model.Pedido;
import com.arquitecturajava.model.Producto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class CRUD {

	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("R1-UD3");

	public static void main(String[] args) {

		crearCliente("Samuel", "Samuel@gmail.com");
		crearCliente("Pedro", "Pedro@gmail.com");
		crearCliente("Ana", "Ana@gmail.com");

		crearProducto(1L, "Teclado mecánico", 79.99);
		crearProducto(2L, "Ratón gaming", 39.90);
		crearProducto(5L, "Monitor FullHD", 149.00);

		List<Long> idProductos = Arrays.asList(1L, 2L, 5L);

		crearPedido(2L, idProductos);

		obtenerCliente(1L);

		actualizarEmailCliente(2L, "nuevoEmail@gmail.com");

		obtenerCliente(2L);

		listaClientes();

		borrarCliente(1L);

		listaClientes();

		emf.close();
	}

	public static void crearCliente(String nombre, String email) {

		EntityManager em = emf.createEntityManager();

		try {
			em.getTransaction().begin();

			Cliente c = new Cliente();
			c.setNombre(nombre);
			c.setEmail(email);

			em.persist(c);

			em.getTransaction().commit();

		} finally {
			em.close();
		}
	}

	public static void crearPedido(Long idCliente, List<Long> idProductos) {

		EntityManager em = emf.createEntityManager();

		try {
			em.getTransaction().begin();

			Cliente cliente = em.find(Cliente.class, idCliente);

			Pedido pedido = new Pedido();
			pedido.setFecha(LocalDate.now());
			pedido.setCliente(cliente);

			// Añadir productos al pedido
			for (Long idProd : idProductos) {
				Producto p = em.find(Producto.class, idProd);
				if (p != null) {
					pedido.getProductos().add(p);
				}
			}

			em.persist(pedido);

			cliente.getPedidos().add(pedido);

			em.getTransaction().commit();

		} finally {
			em.close();
		}
	}

	public static void obtenerCliente(Long idCliente) {

		EntityManager em = emf.createEntityManager();

		try {
			System.out.println(em.find(Cliente.class, idCliente).toString());
			obtenerPedidos(idCliente);

		} finally {
			em.close();
		}
	}

	private static void obtenerPedidos(Long idCliente) {

		EntityManager em = emf.createEntityManager();

		try {
			List<Pedido> pedidos = em.createQuery("SELECT p FROM Pedido p WHERE p.cliente.id = :id", Pedido.class)
					.setParameter("id", idCliente).getResultList();

			System.out.println("Pedidos del cliente " + idCliente + ":");

			if (pedidos.isEmpty()) {
				System.out.println("\tEste cliente no tiene pedidios");

			} else {
				for (Pedido pedido : pedidos) {
					System.out.println("\tPedido ID: " + pedido.getId() + " | Fecha: " + pedido.getFecha());
				}
			}
			System.out.println("\n");

		} finally {
			em.close();
		}
	}

	public static void actualizarEmailCliente(Long idCliente, String nuevoEmail) {

		EntityManager em = emf.createEntityManager();

		try {
			em.getTransaction().begin();

			Cliente c = em.find(Cliente.class, idCliente);

			if (c != null) {
				c.setEmail(nuevoEmail);
			}

			em.getTransaction().commit();

		} finally {
			em.close();
		}
	}

	public static void borrarCliente(Long idCliente) {

		EntityManager em = emf.createEntityManager();

		try {
			em.getTransaction().begin();

			Cliente c = em.find(Cliente.class, idCliente);

			if (c != null) {
				em.remove(c);
			}

			em.getTransaction().commit();

		} finally {
			em.close();
		}
	}

	public static void crearProducto(Long idProducto, String nombre, double precio) {

		EntityManager em = emf.createEntityManager();

		try {
			em.getTransaction().begin();

			Producto p = new Producto();
			p.setId(idProducto);
			p.setNombre(nombre);
			p.setPrecio(precio);

			em.persist(p);

			em.getTransaction().commit();

		} finally {
			em.close();
		}
	}

	public static void listaClientes() {

		EntityManager em = emf.createEntityManager();

		try {
			em.getTransaction().begin();

			// Consulta JPQL moderna
			List<Cliente> clientes = em.createQuery("SELECT p FROM Cliente p", Cliente.class).getResultList();

			System.out.println("Clientes:");
			for (Cliente c : clientes) {
				System.out.println("\tNombre: " + c.getNombre() + ", Email: " + c.getEmail() + c.getPedidos());
			}
			System.out.println("\n");

			em.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		} finally {
			em.close();
		}

	}

}
