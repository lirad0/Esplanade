package com.esplanade.backend.seed;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.esplanade.backend.model.TableauCardData;
import com.esplanade.backend.model.TableauBitData;
import com.esplanade.backend.repository.TableauCardRepository;
import com.esplanade.backend.repository.TableauBitRepository;

@Component
public class DataSeeder implements ApplicationRunner {
    private final TableauCardRepository tableauCardRepository;
    private final TableauBitRepository TableauBitRepository;

    public DataSeeder(TableauCardRepository tableauCardRepository, TableauBitRepository TableauBitRepository) {
        this.tableauCardRepository = tableauCardRepository;
        this.TableauBitRepository = TableauBitRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (tableauCardRepository.count() == 0) {
            TableauCardData exampleUrl1 = new TableauCardData();
            exampleUrl1.setName("Example Url 1");
            exampleUrl1.setImageName("img.png");
            exampleUrl1.setUrl("https://placehold.co/600x400");

            TableauCardData exampleUrl2 = new TableauCardData();
            exampleUrl2.setName("Example Url 2");
            exampleUrl2.setImageName("img.svg");
            exampleUrl2.setUrl("https://placehold.co/600x400/000000/FFFFFF.png");

            tableauCardRepository.saveAll(List.of(exampleUrl1, exampleUrl2));
        }

        if (TableauBitRepository.count() == 0) {
            TableauBitData weatherWidget = new TableauBitData();
            weatherWidget.setUrl("https://api.wo-cloud.com/content/widget/?geoObjectKey=10828681&language=it&region=IT&timeFormat=HH:mm&windUnit=kmh&systemOfMeasurement=metric&temperatureUnit=celsius");
            TableauBitRepository.save(weatherWidget);
        }
    }
}
