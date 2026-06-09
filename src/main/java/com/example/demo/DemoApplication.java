package com.example.demo;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.demo.domain.Anuncio;
import com.example.demo.domain.Categoria;
import com.example.demo.domain.Estado;
import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.services.AnuncioService;
import com.example.demo.services.CategoriaService;
import com.example.demo.services.UsuarioService;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

        public static void main(String[] args) {
                SpringApplication.run(DemoApplication.class, args);
        }

        @Bean
        @Profile("dev")
        CommandLineRunner initData(AnuncioService serviceAnuncio, UsuarioService serviceUsuario, CategoriaService servicioCategoria) {
                return args -> {
                        // categorías
                        Categoria emisoras = servicioCategoria.añadir(new Categoria(null, "Emisoras"));
                        Categoria antenas = servicioCategoria.añadir(new Categoria(null, "Antenas"));
                        Categoria walkieTalkies = servicioCategoria.añadir(new Categoria(null, "Walkie Talkies"));
                        Categoria amplificadores = servicioCategoria.añadir(new Categoria(null, "Amplificadores"));
                        Categoria fuentesAlimentacion = servicioCategoria
                                        .añadir(new Categoria(null, "Fuentes de alimentación"));
                        Categoria medidoresAnalizadores = servicioCategoria
                                        .añadir(new Categoria(null, "Medidores y analizadores"));
                        Categoria accesorios = servicioCategoria.añadir(new Categoria(null, "Accesorios"));
                        Categoria componentesElectronicos = servicioCategoria
                                        .añadir(new Categoria(null, "Componentes electrónicos"));
                        Categoria librosYManuales = servicioCategoria.añadir(new Categoria(null, "Libros y manuales"));
                        Categoria otros = servicioCategoria.añadir(new Categoria(null, "Otros"));

                        // usuarios
                        Usuario admin = new Usuario();
                        admin.setNombre("admin");
                        admin.setEmail("admin@gmail.com");
                        admin.setFechaRegistro(LocalDate.now());
                        admin.setRol(Rol.ADMIN);
                        admin.setPassword("123456");

                        Usuario user1 = new Usuario();
                        user1.setNombre("usuario1");
                        user1.setEmail("usuario1@gmail.com");
                        user1.setFechaRegistro(LocalDate.now());
                        user1.setRol(Rol.USER);
                        user1.setPassword("123456");

                        Usuario user2 = new Usuario();
                        user2.setNombre("usuario2");
                        user2.setEmail("usuario2@gmail.com");
                        user2.setFechaRegistro(LocalDate.now());
                        user2.setRol(Rol.USER);
                        user2.setPassword("123456");

                        serviceUsuario.añadir(admin);
                        serviceUsuario.añadir(user1);
                        serviceUsuario.añadir(user2);

                        // EMISORAS
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Yaesu FT-857D").precio(350D)
                                        .estado(Estado.Muy_bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Transceptor multibanda en muy buen estado, incluye micrófono original y manual.")
                                        .categoria(emisoras).usuario(user1).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Icom IC-7300").precio(900D)
                                        .estado(Estado.Perfecto).fechaPublicacion(LocalDate.now())
                                        .descripcion("Emisora HF de última generación con pantalla táctil. Como nueva.")
                                        .categoria(emisoras).usuario(user2).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Kenwood TS-590SG").precio(750D)
                                        .estado(Estado.Bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Transceptor HF/50MHz, excelente receptor, incluye micrófono.")
                                        .categoria(emisoras).usuario(user1).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Yaesu FT-991A").precio(650D)
                                        .estado(Estado.Muy_bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Multibanda, multimodo. VHF/UHF/HF todo en uno.")
                                        .categoria(emisoras).usuario(user2).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Icom IC-705").precio(1100D)
                                        .estado(Estado.Perfecto).fechaPublicacion(LocalDate.now())
                                        .descripcion("Emisora portátil HF/VHF/UHF con batería integrada. Ideal para portable.")
                                        .categoria(emisoras).usuario(user1).build());

                        // ANTENAS
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Antena Diamond X50").precio(45D)
                                        .estado(Estado.Bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Antena bibanda VHF/UHF para uso fijo, buen estado general.")
                                        .categoria(antenas).usuario(user2).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Cushcraft R7000").precio(180D)
                                        .estado(Estado.Aceptable).fechaPublicacion(LocalDate.now())
                                        .descripcion("Antena vertical HF multibanda, algunos arañazos pero funciona perfectamente.")
                                        .categoria(antenas).usuario(user1).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Yagi 144MHz casera").precio(30D)
                                        .estado(Estado.Bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Antena yagi de 5 elementos para VHF, construcción propia con materiales de calidad.")
                                        .categoria(antenas).usuario(user2).build());

                        // WALKIE TALKIES
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Baofeng UV-5R").precio(25D)
                                        .estado(Estado.Bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Walkie bibanda clásico, funciona bien, incluye cargador y auricular.")
                                        .categoria(walkieTalkies).usuario(user1).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Yaesu FT-60R").precio(120D)
                                        .estado(Estado.Muy_bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Portátil robusto bibanda, resistente al agua, batería con buena autonomía.")
                                        .categoria(walkieTalkies).usuario(user2).build());

                        // AMPLIFICADORES
                        serviceAnuncio.añadir(Anuncio.builder().nombre("RM Italy KL-500").precio(200D)
                                        .estado(Estado.Bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Amplificador lineal HF, 500W PEP, funciona correctamente.")
                                        .categoria(amplificadores).usuario(user1).build());

                        // FUENTES DE ALIMENTACIÓN
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Powerwerx SS-30DV").precio(85D)
                                        .estado(Estado.Perfecto).fechaPublicacion(LocalDate.now())
                                        .descripcion("Fuente de alimentación 30A conmutada, sin ruido, ideal para equipos móviles.")
                                        .categoria(fuentesAlimentacion).usuario(user2).build());

                        // MEDIDORES Y ANALIZADORES
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Nissei RS-40 SWR").precio(40D)
                                        .estado(Estado.Bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Medidor de ROE y potencia para HF/VHF, fácil de usar.")
                                        .categoria(medidoresAnalizadores).usuario(user1).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("NanoVNA V2").precio(65D)
                                        .estado(Estado.Muy_bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Analizador de antenas vectorial portátil, incluye estuche y cables.")
                                        .categoria(medidoresAnalizadores).usuario(user2).build());

                        // ACCESORIOS
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Micrófono Heil PR-40").precio(150D)
                                        .estado(Estado.Perfecto).fechaPublicacion(LocalDate.now())
                                        .descripcion("Micrófono de radiodifusión de alta calidad, ideal para contest.")
                                        .categoria(accesorios).usuario(user1).build());
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Manipulador Bencher BY-1").precio(90D)
                                        .estado(Estado.Muy_bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Manipulador de paleta doble para CW, ajuste fino, base pesada.")
                                        .categoria(accesorios).usuario(user2).build());

                        // COMPONENTES ELECTRÓNICOS
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Lote condensadores cerámicos").precio(10D)
                                        .estado(Estado.Perfecto).fechaPublicacion(LocalDate.now())
                                        .descripcion("Surtido de condensadores cerámicos de distintos valores, ideal para proyectos.")
                                        .categoria(componentesElectronicos).usuario(user1).build());

                        // LIBROS Y MANUALES
                        serviceAnuncio.añadir(Anuncio.builder().nombre("ARRL Handbook 2022").precio(35D)
                                        .estado(Estado.Muy_bueno).fechaPublicacion(LocalDate.now())
                                        .descripcion("Manual de referencia para radioaficionados, edición 2022 en inglés.")
                                        .categoria(librosYManuales).usuario(user2).build());

                        // OTROS
                        serviceAnuncio.añadir(Anuncio.builder().nombre("Torre telescópica 6m").precio(120D)
                                        .estado(Estado.Aceptable).fechaPublicacion(LocalDate.now())
                                        .descripcion("Torre telescópica de aluminio de 6 metros, desmontable, usada pero funcional.")
                                        .categoria(otros).usuario(user1).build());
                };
        }
}