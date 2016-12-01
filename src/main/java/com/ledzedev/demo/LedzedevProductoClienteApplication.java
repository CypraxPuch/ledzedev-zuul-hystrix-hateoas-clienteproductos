package com.ledzedev.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Código generado por Gerado Pucheta Figueroa
 * Twitter: @ledzedev
 * http://ledze.mx
 * 01/Dic/2016.
 */
@EnableCircuitBreaker
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class LedzedevProductoClienteApplication {
	public static void main(String[] args) {
		SpringApplication.run(LedzedevProductoClienteApplication.class, args);
	}
}

/**
 * Código generado por Gerado Pucheta Figueroa
 * Twitter: @ledzedev
 * http://ledze.mx
 * 01/Dic/2016.
 */
@RestController
class ProductoApiGatewayRestController {
	private static final Logger LOG = LoggerFactory.getLogger(ProductoApiGatewayRestController.class);

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	};

	@Autowired
	RestTemplate restTemplate;

	public Collection<String> getNombreProductosFallback(){
		return Arrays.asList("Error al obtener los nombres de los productos en la tienda de la esquina.");
	}

	@HystrixCommand(fallbackMethod = "getNombreProductosFallback")
	@RequestMapping(method = RequestMethod.GET, value = "/nomprod")
	public Collection<String> getNombreProductos(){
		LOG.info("Obteniendo nombre de productos");

		ParameterizedTypeReference<Resources<Producto>> ptr =
				new ParameterizedTypeReference<Resources<Producto>>() { };

		ResponseEntity<Resources<Producto>> entity = this.restTemplate.exchange("http://producto-service/productoes", HttpMethod.GET, null, ptr);

		return entity.getBody()
				.getContent()
				.stream()
				.map(Producto::getNombreProducto)
				.collect(Collectors.toList());
	}
}

/**
 * Código generado por Gerado Pucheta Figueroa
 * Twitter: @ledzedev
 * http://ledze.mx
 * 01/Dic/2016.
 */
class Producto {
	private String nombreProducto;

	public String getNombreProducto() {
		return nombreProducto;
	}
}
