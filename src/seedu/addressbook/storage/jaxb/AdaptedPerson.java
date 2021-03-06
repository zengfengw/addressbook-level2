package seedu.addressbook.storage.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import seedu.addressbook.common.Utils;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.DOB;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList;

/**
 * JAXB-friendly adapted person data holder class.
 */
public class AdaptedPerson {

    private static class AdaptedContactDetail {
        @XmlValue
        public String value;
        @XmlAttribute(required = true)
        public boolean isPrivate;
    }

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private AdaptedContactDetail phone;
    @XmlElement(required = true)
    private AdaptedContactDetail dob;
    @XmlElement(required = true)
    private AdaptedContactDetail email;
    @XmlElement(required = true)
    private AdaptedContactDetail address;

    @XmlElement
    private List<AdaptedTag> tagged = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedPerson() {}


    /**
     * Converts a given Person into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedPerson
     */
    public AdaptedPerson(ReadOnlyPerson source) {
        name = source.getName().fullName;

        phone = new AdaptedContactDetail();
        phone.isPrivate = source.getPhone().isPrivate();
        phone.value = source.getPhone().value;

        dob = new AdaptedContactDetail();
        dob.isPrivate = source.getDOB().isPrivate();
        dob.value = source.getDOB().value;

        email = new AdaptedContactDetail();
        email.isPrivate = source.getEmail().isPrivate();
        email.value = source.getEmail().value;

        address = new AdaptedContactDetail();
        address.isPrivate = source.getAddress().isPrivate();
        address.value = source.getAddress().value;

        tagged = new ArrayList<>();
        for (Tag tag : source.getTags()) {
            tagged.add(new AdaptedTag(tag));
        }
    }

    /**
     * Returns true if any required field is missing.
     *
     * JAXB does not enforce (required = true) without a given XML schema.
     * Since we do most of our validation using the data class constructors, the only extra logic we need
     * is to ensure that every xml element in the document is present. JAXB sets missing elements as null,
     * so we check for that.
     */
    public boolean isAnyRequiredFieldMissing() {
        for (AdaptedTag tag : tagged) {
            if (tag.isAnyRequiredFieldMissing()) {
                return true;
            }
        }
        // second call only happens if phone/dob/email/address are all not null
        return Utils.isAnyNull(name, phone, dob, email, address)
                || Utils.isAnyNull(phone.value, dob.value, email.value, address.value);
    }

    /**
     * Converts this jaxb-friendly adapted person object into the Person object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (AdaptedTag tag : tagged) {
            personTags.add(tag.toModelType());
        }
        final Name name = new Name(this.name);
        final Phone phone = new Phone(this.phone.value, this.phone.isPrivate);
        final DOB dob = new DOB(this.dob.value, this.dob.isPrivate);
        final Email email = new Email(this.email.value, this.email.isPrivate);
        final Address address = new Address(this.address.value, this.address.isPrivate);
        final UniqueTagList tags = new UniqueTagList(personTags);
        return new Person(name, phone, dob, email, address, tags);
    }
}
