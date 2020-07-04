package com.example.utils.converters;

import com.example.business.models.ProducerDetailModel;
import com.example.business.models.ProducerModel;
import com.example.database.entity.Image;
import com.example.database.entity.Producer;
import com.example.rest.dtos.ProducerDto;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

public interface ProducerConverter {

    static ProducerModel convertToModel(Producer producer) {

        Set<ProducerDetailModel> details = ofNullable(producer.getDetails()).orElse(emptySet())
                .stream()
                .map(d -> ProducerDetailConverter.convertToModel(d))
                .collect(Collectors.toSet());

        return ProducerModel.builder()
                .id(producer.getId())
                .details(details)
                .image(ImageConverter.convertToModel(
                        ofNullable(producer.getImage()).orElse(Image.builder().build())))
                .build();
    }

    static ProducerDto convertToDto(ProducerModel producer, String lang, String defaultLang) {

        ProducerDetailModel details = ProducerDetailConverter.getProducerDetailByLanguage(producer, lang, defaultLang);

        return ProducerDto.builder()
                .id(producer.getId())
                .lang(details.getLang())
                .name(details.getName())
                .description(details.getDescription())
                .image(ImageConverter.convertToDto(producer.getImage()))
                .build();
    }
}
