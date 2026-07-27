package com.KartaGalaxy.backend.seed;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.KartaGalaxy.backend.model.TableauCardData;
import com.KartaGalaxy.backend.model.UrlOnlyItemData;
import com.KartaGalaxy.backend.repository.TableauCardRepository;
import com.KartaGalaxy.backend.repository.UrlOnlyItemRepository;

@Component
public class DataSeeder implements ApplicationRunner {
    private final TableauCardRepository tableauCardRepository;
    private final UrlOnlyItemRepository urlOnlyItemRepository;

    public DataSeeder(TableauCardRepository tableauCardRepository, UrlOnlyItemRepository urlOnlyItemRepository) {
        this.tableauCardRepository = tableauCardRepository;
        this.urlOnlyItemRepository = urlOnlyItemRepository;
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

        if (urlOnlyItemRepository.count() == 0) {
            UrlOnlyItemData weatherWidget = new UrlOnlyItemData();
            weatherWidget.setUrl("https://api.wo-cloud.com/content/widget/?geoObjectKey=10828681&language=it&region=IT&timeFormat=HH:mm&windUnit=kmh&systemOfMeasurement=metric&temperatureUnit=celsius");
            urlOnlyItemRepository.save(weatherWidget);
        }
    }
}
