package at.nic.rdap.sample;

import be.dnsbelgium.core.DomainName;
import be.dnsbelgium.core.TelephoneNumber;
import be.dnsbelgium.rdap.core.*;
import be.dnsbelgium.rdap.service.impl.DefaultDomainService;
import be.dnsbelgium.vcard.Contact;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyDomainService extends DefaultDomainService {

  protected final DateTime createTime = DateTime.now().toDateTime(DateTimeZone.UTC).minusDays(200);
  protected final DateTime lastChangedTime = createTime.plusDays(100);

    private final static Logger logger = LoggerFactory.getLogger(MyDomainService.class);

  @Override
  public Domain getDomainImpl(DomainName domainName) throws RDAPError {

    logger.info("Received query for " + domainName.getStringValue());

    String tld = domainName.getTLDLabel().getStringValue();
    if (!tld.equalsIgnoreCase("at")) {
      throw RDAPError.notAuthoritative(domainName);
    }
    if (domainName.getStringValue().equals("available.at")) {
      throw RDAPError.noResults(domainName.getStringValue());
    }

    try {
      Domain domain = new Domain(
              someLinks(),
              someNotices(0),
              someRemarks(0),
              "en",
              someEvents(),
              someStatuses(),
              DomainName.of("whois.nic.at"),
              "Handle",
              domainName,
              domainName,
              someVariants(),
              someNameservers(),
              aSecureDNS(),
              someEntities(),
              somePublicIds(),
              anIPNetwork()
      );
      domain.addRdapConformance(Domain.DEFAULT_RDAP_CONFORMANCE);

      logger.info("Sending data for " + domainName.getStringValue());

      return domain;

    } catch (Exception e) {

      e.printStackTrace();
      throw new InternalServerError();

    }
  }


    protected List<Link> someLinks() throws Exception {
    Set<String> hrefLang = new HashSet<String>();
    hrefLang.add("en");
    //hrefLang.add("de");
    List<String> title = new ArrayList<String>();
    title.add("Title part 1");
    title.add("Title part 2");
    List<Link> links = new ArrayList<Link>();
    links.add(new Link(new URI("http://example.com/value"), "rel", new URI("http://example.com/href"), hrefLang, "title", "Media", "Type") );
    return links;
  }

  protected List<Notice> someNotices(int count) throws Exception {
    List<Notice> notices = new ArrayList<Notice>();
    for (int i=0; i<count; i++) {
        notices.add(aNoticeOrRemark());
        notices.add(aNoticeOrRemark());
    }
    return notices;
  }

  protected List<Notice> someRemarks(int count) throws Exception {
    List<Notice> remarks = new ArrayList<Notice>();
    for (int i=0; i<count; i++) {
        remarks.add(aNoticeOrRemark());
        remarks.add(aNoticeOrRemark());
    }
    return remarks;
  }

  protected Notice aNoticeOrRemark() throws Exception {
    List<String> description = new ArrayList<String>();
    description.add("Description part 1");
    description.add("Description part 2");
    return new Notice("Title", "Type", description, someLinks());
  }

  protected List<Event> someEvents() throws Exception {
    List<Event> events = new ArrayList<Event>();
    events.add(new Event(Event.Action.Default.REGISTRATION, "EventActor", createTime, someLinks()));
    events.add(new Event(Event.Action.Default.LAST_CHANGED, "EventActor", lastChangedTime, someLinks()));
    return events;
  }

  protected List<Status> someStatuses() throws Exception {
    List<Status> statuses = new ArrayList<Status>();
    statuses.add(Status.Default.ACTIVE);
    statuses.add(Status.Default.DELETE_PROHIBITED);
    statuses.add(Status.Factory.of("some specific status"));
    return statuses;
  }

  protected List<Nameserver> someNameservers() throws Exception {
    List<Nameserver> nameservers = new ArrayList<Nameserver>();
    nameservers.add(new Nameserver(someLinks(), someNotices(0), someRemarks(0), "en", someEvents(), someStatuses(), DomainName.of("whois.example.com"), "Handle", DomainName.of("ns.xn--exmple-jta.com"), DomainName.of("ns.exàmple.com"), someIpAddresses()));
    nameservers.add(new Nameserver(someLinks(), someNotices(0), someRemarks(0), "en", someEvents(), someStatuses(), DomainName.of("whois.example.com"), "Handle", DomainName.of("ns.xn--exmple-jta.com"), DomainName.of("ns.exàmple.com"), someIpAddresses()));
    return nameservers;
  }

  protected Nameserver.IpAddresses someIpAddresses() {
    List<String> v4s = new ArrayList<String>();
    v4s.add("193.5.6.198");
    v4s.add("89.65.3.87");
    List<String> v6s = new ArrayList<String>();
    v6s.add("2001:678:9::1");
    v6s.add("FE80:0000:0000:0000:0202:B3FF:FE1E:8329");
    return new Nameserver.IpAddresses(v4s, v6s);
  }

  protected List<PublicId> somePublicIds() {
    List<PublicId> publicIds = new ArrayList<PublicId>();
    publicIds.add(new PublicId("Type", "Identifier"));
    publicIds.add(new PublicId("Type", "Identifier"));
    return publicIds;
  }

  protected List<Entity.Role> someRoles() {
    List<Entity.Role> roles = new ArrayList<Entity.Role>();
    roles.add(Entity.Role.Default.REGISTRANT);
    roles.add(Entity.Role.Default.ADMINISTRATIVE);
    return roles;
  }

  @SuppressWarnings("UnusedDeclaration")
  protected Contact aContact() {
    return new Contact.Builder()
            .setFormattedName("Larry Ellison")
            .setGivenName("Larry")
            .setFamilyName("Ellison")
            .setOrganization("Retirees Inc.")
            .addOU("This is an OU")
            .addOU("This is another OU")
            .addStreet("street 1")
            .addStreet("street 2")
            .addLocality("New York")
            .addLocality("Brooklyn")
            .addRegion("New York")
            .addRegion("East coast")
            .addPostalCode("12345")
            .addCountry("United states of America")
            .addTelephoneNumber(TelephoneNumber.of(32, BigInteger.valueOf(123456)))
            .addTelephoneNumber(TelephoneNumber.of("+32.654321"))
            .addFaxNumber(TelephoneNumber.of(32, BigInteger.valueOf(987654)))
            .addEmailAddress("larry.ellison@retirees.com")
            .addEmailAddress("le@former.oracle.com")
            .setLanguages("en", "de", "es")
            .build();
  }

  protected List<Domain.Variant> someVariants() {
    List<Domain.Variant.Relation> relations1 = new ArrayList<Domain.Variant.Relation>();
    relations1.add(Domain.Variant.Relation.Default.UNREGISTERED);
    relations1.add(Domain.Variant.Relation.Default.RESTRICTED_REGISTRATION);
    List<Domain.Variant.Name> names1 = new ArrayList<Domain.Variant.Name>();
    names1.add(new Domain.Variant.Name(DomainName.of("exomple.com"), DomainName.of("exomple.com")));
    names1.add(new Domain.Variant.Name(DomainName.of("eximple.com"), DomainName.of("eximple.com")));
    List<Domain.Variant.Relation> relations2 = new ArrayList<Domain.Variant.Relation>();
    relations2.add(Domain.Variant.Relation.Default.REGISTERED);
    List<Domain.Variant.Name> names2 = new ArrayList<Domain.Variant.Name>();
    //names2.add(new Domain.Variant.Name(DomainName.of("xn--exmple-jta.com"), DomainName.of("exàmple.com")));
    List<Domain.Variant> variants = new ArrayList<Domain.Variant>();
    variants.add(new Domain.Variant(relations1, "IdnTable", names1));
    variants.add(new Domain.Variant(relations2, "IdnTable2", names2));
    return variants;
  }

  protected SecureDNS aSecureDNS() {
    SecureDNS.DSData dsData1 = new SecureDNS.DSData(64156, 8, "DC48B4183F9AC496574DEB8633F627A6DE207493", 1, null, null);
    SecureDNS.DSData dsData2 = new SecureDNS.DSData(64156, 8, "DE3BBED2664B02B9FEC6FF81D8539B14A5714A2C7A92E8FE58914200 C30B1958", 2, null, null);
    List<SecureDNS.DSData> dsList = new ArrayList<SecureDNS.DSData>();
    dsList.add(dsData1);
    dsList.add(dsData2);
    return new SecureDNS(true, true, 6000, dsList, null);
  }

  protected IPNetwork anIPNetwork() throws Exception {
    return new IPNetwork(someLinks(), someNotices(0), someRemarks(0), "en", IPNetwork.OBJECT_CLASS_NAME, someEvents(), someStatuses(), DomainName.of("whois.example.com"), "Handle", InetAddress.getByName("193.12.32.98"), InetAddress.getByName("193.12.32.98"), "Name", "Type", "Country", "ParentHandle", someEntities());
  }

  protected List<Entity> someEntities() {
    List<Entity> entityList = new ArrayList<Entity>();
    Contact vCard = new Contact.Builder()
            .addOU("White House")
            .addStreet("350 Palm Drive")
            .addPostalCode("DC 14580")
            .addLocality("Washington")
            .addRegion("DC")
            .addTelephoneNumber(TelephoneNumber.of("1.350700"))
            .addCountry("USA")
            .addEmailAddress("john.f.kennedy@whitehouse.gov")
            .setFormattedName("JFK")
            .build();
    //Entity registrant = new Entity(null, null, null, "en", Entity.OBJECT_CLASS_NAME, null, null, null, "REGISTRANT", vCard, someRoles(), null, null);
    //Entity registrant = new Entity();
    //entityList.add(registrant);
    return entityList;
  }

}