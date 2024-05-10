package com.ceica.padel.Service;

import com.ceica.padel.Model.*;
import com.ceica.padel.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReservaService {

    private final HorarioRepository horarioRepository;

    private final ReservaRepository reservaRepository;

    private final UsuarioReservaRepository usuarioReservaRepository;

    private final HistoricoRepository historicoRepository;

    private final PistaRepository pistaRepository;


    @Autowired
    public ReservaService(HorarioRepository horarioRepository,
                          ReservaRepository reservaRepository,
                          UsuarioReservaRepository usuarioReservaRepository,
                          HistoricoRepository historicoRepository,
                          PistaRepository pistaRepository) {
        this.horarioRepository = horarioRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioReservaRepository = usuarioReservaRepository;
        this.historicoRepository = historicoRepository;
        this.pistaRepository = pistaRepository;
    }

    public List<String> getHorariosCompletoByIdPista(int idpista, LocalDate fecha) {
        List<Reserva> reservaList = reservaRepository.findAllByFechaAndIdpista(fecha, idpista);
        List<String> availableHorarios = new ArrayList<>();
        if (reservaList.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                availableHorarios.add("1" + i + ":00");
            }
            for (int i = 0; i < 3; i++) {
                availableHorarios.add("2" + i + ":00");
            }
            return availableHorarios;
        }
        List<Horario> horarioList = horarioRepository.findAll();
        List<Integer> reservaTimes = new ArrayList<>();
        for (Reserva reserva : reservaList)
            reservaTimes.add(reserva.getIdhorario());
        horarioList.removeIf(horario -> reservaTimes.contains(horario.getIdhorario()));
        for (Horario horario : horarioList)
            availableHorarios.add(horario.getHora());
        return availableHorarios;
    }

    public List<String> getHorariosPlazaByIdPista(int idpista, LocalDate fecha) {
        List<Reserva> reservaList = reservaRepository.findAllByFechaAndIdpista(fecha, idpista);
        List<String> availableHorarios = new ArrayList<>();
        if (reservaList.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                availableHorarios.add("1" + i + ":00");
            }
            for (int i = 0; i < 3; i++) {
                availableHorarios.add("2" + i + ":00");
            }
            return availableHorarios;
        }
        List<Integer> idCompletos = getIntegers(reservaList);
        List<Horario> horarioList = horarioRepository.findAll();
        horarioList.removeIf(horario -> idCompletos.contains(horario.getIdhorario()));
        for (Horario horario : horarioList){
            availableHorarios.add(horario.getHora());
        }
        return availableHorarios;
    }

    private static List<Integer> getIntegers(List<Reserva> reservaList) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Reserva reserva : reservaList) {
            if (map.containsKey(reserva.getIdhorario())) {
                map.put(reserva.getIdhorario(), map.get(reserva.getIdhorario()) + 1);
            } else {
                map.put(reserva.getIdhorario(), 1);
            }
        }
        List<Integer> idCompletos = new ArrayList<>();
        for (Map.Entry<Integer, Integer> clave : map.entrySet()) {
            Integer id = clave.getKey();
            if (clave.getValue() == 4) {
                idCompletos.add(id);
            }
        }
        return idCompletos;
    }

    public boolean save(LocalDate fecha, Integer idHorario, Integer idPista, Usuario userLogged) {
        try {
            Reserva reserva = reservaRepository.save(new Reserva(LocalDateTime.now(), fecha, idHorario, idPista));
            usuarioReservaRepository.save(new UsuarioReserva(userLogged.getIdusuario(), reserva.getIdreserva(), false));
            return true;
        } catch (Exception excepcion){
            return false;
        }
    }

    public boolean saveComplete(LocalDate fecha, Integer idHorario, Integer idPista, Usuario userLogged) {
        try {
            Reserva reserva = reservaRepository.save(new Reserva(LocalDateTime.now(), fecha, idHorario, idPista));
            for (int i = 0; i < 4; i++) {
                usuarioReservaRepository.save(new UsuarioReserva(userLogged.getIdusuario(), reserva.getIdreserva(), false));
            }
            return true;
        } catch (Exception excepcion){
            return false;
        }
    }

    public List<Historico> getHistoricoByIdusuario(Integer idusuario) {
        List<Historico> historicos = historicoRepository.findAllByIdusuario(idusuario);
        Set<LocalDate> fechas = new HashSet<>();
        List<Historico> historicosSinFechasRepetidas = new ArrayList<>();
        for (Historico historico : historicos) {
            LocalDate fecha = historico.getFecha();
            if (!fechas.contains(fecha)) {
                historicosSinFechasRepetidas.add(historico);
                fechas.add(fecha);
            }
        }
        return historicosSinFechasRepetidas;
    }

    public List<Pista> getAllPistas(){
        return pistaRepository.findAll();
    }

    public Horario getHorarioByHora(String hora){
        return horarioRepository.findHorarioByHora(hora);
    }
}
