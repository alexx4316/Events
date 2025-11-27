package com.Events.Tickets.infraestructura.validation;

import com.Events.Tickets.infraestructura.adapters.in.web.dto.request.EventRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<DateRange, EventRequestDTO> {
    @Override
    public void initialize(DateRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(EventRequestDTO eventDto, ConstraintValidatorContext context) {

        // 1. Verificar si las fechas son nulas (la validación @NotNull debe encargarse, pero es una buena práctica)
        if (eventDto.getStartDate() == null || eventDto.getEndDate() == null) {
            return true;
        }

        // 2. Lógica de validación cruzada: startDate debe ser anterior a endDate
        boolean isValid = eventDto.getStartDate().isBefore(eventDto.getEndDate());

        if (!isValid) {
            // 3. Si la validación falla, personalizamos el mensaje para que apunte a un campo específico (endDate)
            context.disableDefaultConstraintViolation(); // Deshabilita el mensaje por defecto
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()) // Usa el mensaje del @DateRange
                    .addPropertyNode("endDate") // Apunta el error al campo 'endDate'
                    .addConstraintViolation();
        }

        return isValid;
    }
}
