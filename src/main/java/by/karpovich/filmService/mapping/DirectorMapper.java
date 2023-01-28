package by.karpovich.filmService.mapping;

import by.karpovich.filmService.api.dto.DirectorDto;
import by.karpovich.filmService.exception.NotFoundModelException;
import by.karpovich.filmService.jpa.model.CountryModel;
import by.karpovich.filmService.jpa.model.DirectorModel;
import by.karpovich.filmService.jpa.model.FilmModel;
import by.karpovich.filmService.jpa.repository.CountryRepository;
import by.karpovich.filmService.jpa.repository.DirectorRepository;
import by.karpovich.filmService.jpa.repository.FilmRepository;
import by.karpovich.filmService.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DirectorMapper {

    @Autowired
    private Utils utils;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private FilmRepository filmRepository;

    public DirectorDto mapDtoFromModel(DirectorModel model) {
        if (model == null) {
            return null;
        }

        DirectorDto dto = new DirectorDto();

        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDateOfBirth(utils.mapStringFromInstant(model.getDateOfBirth()));
        dto.setPlaceOfBirth(findCountryId(model));
        dto.setFilmsId(findFilmIdFromDirectorModel(model.getId()));

        return dto;
    }

    public DirectorModel mapModelFromDto(DirectorDto dto) {
        if (dto == null) {
            return null;
        }

        DirectorModel model = new DirectorModel();

        model.setName(dto.getName());
        model.setDateOfBirth(utils.mapInstantFromString(dto.getDateOfBirth()));
        model.setPlaceOfBirth(findByIdWhichWillReturnModel(dto.getPlaceOfBirth()));
        model.setFilms(findListFilmsByDirectorId(dto.getFilmsId()));

        return model;
    }

    public List<DirectorDto> mapListDtoFromListModel(List<DirectorModel> modelList) {
        if (modelList == null) {
            return null;
        }

        List<DirectorDto> directorDtoList = new ArrayList<>();

        for (DirectorModel directorModel : modelList) {
            directorDtoList.add(mapDtoFromModel(directorModel));
        }

        return directorDtoList;
    }

    private Long findCountryId(DirectorModel model) {
        Optional<DirectorModel> modelById = directorRepository.findById(model.getId());

        return modelById.get().getPlaceOfBirth().getId();
    }

    private List<Long> findFilmIdFromDirectorModel(Long id) {
        Optional<DirectorModel> model = directorRepository.findById(id);

        List<FilmModel> listFilm = model.get().getFilms();

        return listFilm.stream()
                .map(FilmModel::getId)
                .collect(Collectors.toList());
    }

    private List<FilmModel> findListFilmsByDirectorId(List<Long> listFilmId) {
        List<FilmModel> modelList = new ArrayList<>();

        for (Long id : listFilmId) {
            FilmModel model = findFilmByIdWhichWillReturnModel(id);
            modelList.add(model);
        }

        return modelList;
    }

    public CountryModel findByIdWhichWillReturnModel(Long id) {
        Optional<CountryModel> model = countryRepository.findById(id);

        return model.orElseThrow(
                () -> new NotFoundModelException("the country with ID = " + id + " was not found"));
    }

    public FilmModel findFilmByIdWhichWillReturnModel(Long id) {
        Optional<FilmModel> model = filmRepository.findById(id);

        return model.orElseThrow(
                () -> new NotFoundModelException("the film with ID = " + id + " was not found"));
    }
}
